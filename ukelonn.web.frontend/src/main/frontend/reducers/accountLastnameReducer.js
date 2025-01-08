import { createReducer } from '@reduxjs/toolkit';
import { SELECT_ACCOUNT } from '../actiontypes';
import { api } from '../api';

const defaultValue = '';

const accountLastnameReducer = createReducer(defaultValue, builder => {
    builder
        .addMatcher(api.endpoints.getAccount.matchFulfilled, (state, action) => action.payload.lastName)
        .addMatcher(api.endpoints.postJobRegister.matchFulfilled, (state, action) => action.payload.lastName)
        .addMatcher(api.endpoints.postPaymentRegister.matchFulfilled, (state, action) => action.payload.lastName)
        .addCase(SELECT_ACCOUNT, (state, action) => action.payload.lastName);
});

export default accountLastnameReducer;
