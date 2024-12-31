import { createReducer } from '@reduxjs/toolkit';
import { SELECT_ACCOUNT } from '../actiontypes';
import { api } from '../api';

const defaultValue = 0;

const accountBalanceReducer = createReducer(defaultValue, builder => {
    builder
        .addMatcher(api.endpoints.getAccount.matchFulfilled, (state, action) => action.payload.balance)
        .addMatcher(api.endpoints.postJobRegister.matchFulfilled, (state, action) => action.payload.balance)
        .addMatcher(api.endpoints.postPaymentRegister.matchFulfilled, (state, action) => action.payload.balance)
        .addCase(SELECT_ACCOUNT, (state, action) => action.payload.balance);
});

export default accountBalanceReducer;
