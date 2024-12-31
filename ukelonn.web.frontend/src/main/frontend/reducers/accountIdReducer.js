import { createReducer } from '@reduxjs/toolkit';
import { SELECT_ACCOUNT } from '../actiontypes';
import { api } from '../api';
const defaultValue = -1;

const accountIdReducer = createReducer(defaultValue, builder => {
    builder
        .addMatcher(api.endpoints.getAccount.matchFulfilled, (state, action) => action.payload.accountId)
        .addMatcher(api.endpoints.postJobRegister.matchFulfilled, (state, action) => action.payload.accountId)
        .addMatcher(api.endpoints.postPaymentRegister.matchFulfilled, (state, action) => action.payload.accountId)
        .addCase(SELECT_ACCOUNT, (state, action) => action.payload.accountId);
});

export default accountIdReducer;
