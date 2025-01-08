import { createReducer } from '@reduxjs/toolkit';
import { SELECT_ACCOUNT } from '../actiontypes';
import { api } from '../api';

const defaultValue = '';

const accountFirstnameReducer = createReducer(defaultValue, builder => {
    builder
        .addMatcher(api.endpoints.getAccount.matchFulfilled, (state, action) => action.payload.firstName)
        .addMatcher(api.endpoints.postJobRegister.matchFulfilled, (state, action) => action.payload.firstName)
        .addMatcher(api.endpoints.postPaymentRegister.matchFulfilled, (state, action) => action.payload.firstName)
        .addCase(SELECT_ACCOUNT, (state, action) => action.payload.firstName);
});

export default accountFirstnameReducer;
