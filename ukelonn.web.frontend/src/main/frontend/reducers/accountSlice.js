import { createSlice } from '@reduxjs/toolkit';
import { api } from '../api';

const initialState = {
    accountId: -1,
    username: '',
    firstname: '',
    lastname: '',
    fullname: '',
    balance: 0.
};

function payloadOrInitialState(state, action) {
    return action.payload || { ...initialState };
}

export const accountSlice = createSlice({
    name: 'account',
    initialState,
    reducers: {
        selectAccount: (_, action) => action.payload,
    },
    extraReducers: builder => {
        builder
            .addMatcher(api.endpoints.getAccount.matchFulfilled, payloadOrInitialState)
            .addMatcher(api.endpoints.postJobRegister.matchFulfilled, payloadOrInitialState)
            .addMatcher(api.endpoints.postPaymentRegister.matchFulfilled, payloadOrInitialState)
    },
});

export const { selectAccount } = accountSlice.actions;

export default accountSlice.reducer;
