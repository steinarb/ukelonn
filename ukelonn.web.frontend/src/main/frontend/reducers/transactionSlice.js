import { createSlice } from '@reduxjs/toolkit';
import { isClearTransactionTypeForm, isJobsLoaded } from '../matchers';
import { selectAccount } from './accountSlice';
import { api } from '../api';
import { JOB_TABLE_ROW_CLICK, MODIFY_JOB_DATE } from '../actiontypes';
import { isUnselected } from '../common/reducers';

const initialState = {
    id: -1,
    transactionType: {
        id: -1,
        transactionTypeName: '',
        transactionAmount: null,
        transactionIsWork: false,
        transactionIsWagePayment: false,
    },
    transactionTime: new Date().toISOString(),
    transactionAmount: null,
    paidOut: false,
};

export const transactionSlice = createSlice({
    name: 'transaction',
    initialState,
    reducers: {
        selectJobType: (state, action) => setJobTypeOnTransaction(state, action),
        selectPaymentType: (state, action) => setPaymentTypeOnTransaction(state, action),
        selectJob: (_, action) => action.payload,
        setAmount: updateTransactionAmountOnKeyPress,
        clearTransaction: () => initialStateWithCurrentTime(),
    },
    extraReducers: builder => {
        builder
            .addMatcher(api.endpoints.getPaymenttypes.matchFulfilled, (state, action) => ({ ...state, transactionType: ((action.payload.length && action.payload[0]) || { id: -1 }) }))
            .addCase(JOB_TABLE_ROW_CLICK, (_, action) => ({ ...action.payload, transactionTime: new Date(action.payload.transactionTime).toISOString() }))
            .addCase(MODIFY_JOB_DATE, (state, action) => ({ ...state, transactionTime: action.payload + 'T' + state.transactionTime.split('T')[1] }))
            .addCase(selectAccount, (state, action) => ({ ...state, transactionAmount: action.payload.balance }))
            .addMatcher(isClearTransactionTypeForm, () => initialStateWithCurrentTime())
            .addMatcher(isJobsLoaded, () => initialStateWithCurrentTime())
            .addMatcher(api.endpoints.postJobRegister.matchFulfilled, () => initialStateWithCurrentTime())
            .addMatcher(api.endpoints.postPaymentRegister.matchFulfilled, () => initialStateWithCurrentTime())
    },
});

export const { selectJobType, selectPaymentType, selectJob, setAmount, clearTransaction } = transactionSlice.actions;

export default transactionSlice.reducer;

function setJobTypeOnTransaction(state, action) {
    const transactionType = action.payload;
    const transactionAmount = isUnselected(action.payload.id) ? 0 : action.payload.transactionAmount;

    return ({ ...state, transactionType, transactionAmount })
}

function setPaymentTypeOnTransaction(state, action) {
    const transactionType = action.payload;
    const transactionAmount = isUnselected(action.payload.id) ? 0 : action.payload.transactionAmount;

    return ({ ...state, transactionType, transactionAmount })
}

function initialStateWithCurrentTime() {
    const transactionTime = new Date().toISOString();
    return { ...initialState, transactionTime };
}

function updateTransactionAmountOnKeyPress(state, action) {
    const existingAmount = state.transactionAmount;
    if (existingAmount === 0 && action.payload.endsWith('0')) {
        const transactionAmount = parseFloat(action.payload[0]);
        return { ...state, transactionAmount };
    }

    const transactionAmount = (!action.payload || isNaN(action.payload)) ? 0 : parseFloat(action.payload);
    return { ...state, transactionAmount };
}
