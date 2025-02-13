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

export const accountSlice = createSlice({
    name: 'account',
    initialState,
    reducers: {
        selectAccount: (_, action) => action.payload,
    },
    extraReducers: builder => {
        builder
            .addMatcher(api.endpoints.getAccount.matchFulfilled, (state, action) => action.payload)
            .addMatcher(api.endpoints.postJobRegister.matchFulfilled, (state, action) => action.payload)
            .addMatcher(api.endpoints.postPaymentRegister.matchFulfilled, (state, action) => action.payload)
    },
});

export const { selectAccount } = accountSlice.actions;

export default accountSlice.reducer;
