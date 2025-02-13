import { createSlice } from '@reduxjs/toolkit';
import { isClearTransactionTypeForm } from '../matchers';
import { selectAccount } from './accountSlice';
import { api } from '../api';
import { isUnselected } from '../common/reducers';

const initialState = {
    id: -1,
    transactionTypeName: '',
    transactionAmount: null,
    transactionIsWork: false,
    transactionIsWagePayment: false,
};

export const transactionTypeSlice = createSlice({
    name: 'transactionType',
    initialState,
    reducers: {
        selectJobType: (_, action) => action.payload.id === -1 ? initialState : action.payload,
        selectPaymentType: (_, action) => action.payload,
        setName: (state, action) => ({ ...state, transactionTypeName: action.payload }),
        setAmount: (state, action) => ({ ...state, transactionAmount: action.payload }),
        clearTransactionType: () => initialState,
    },
    extraReducers: builder => {
        builder
            .addMatcher(isClearTransactionTypeForm, () => initialState)
    },
});

export const { selectJobType, selectPaymentType, setName, setAmount, clearTransactionType } = transactionTypeSlice.actions;

export default transactionTypeSlice.reducer;
