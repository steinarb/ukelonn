import { createReducer } from '@reduxjs/toolkit';
import { SELECT_ACCOUNT } from '../actiontypes';
import { api } from '../api';

const defaultValue = '';

const accountFullnameReducer = createReducer(defaultValue, builder => {
    builder
        .addMatcher(api.endpoints.getAccount.matchFulfilled, (state, action) => action.payload.fullName)
        .addMatcher(api.endpoints.postJobRegister.matchFulfilled, (state, action) => action.payload.fullName)
        .addMatcher(api.endpoints.postPaymentRegister.matchFulfilled, (state, action) => action.payload.fullName)
        .addCase(SELECT_ACCOUNT, (state, action) => action.payload.fullName);
});

export default accountFullnameReducer;
