import { createReducer } from '@reduxjs/toolkit';
import { SELECT_ACCOUNT } from '../actiontypes';
import { api } from '../api';

const defaultValue = '';

const accountUsernameReducer = createReducer(defaultValue, builder => {
    builder
        .addMatcher(api.endpoints.getAccount.matchFulfilled, (state, action) => action.payload.username)
        .addMatcher(api.endpoints.postJobRegister.matchFulfilled, (state, action) => action.payload.username)
        .addMatcher(api.endpoints.postPaymentRegister.matchFulfilled, (state, action) => action.payload.username)
        .addCase(SELECT_ACCOUNT, (state, action) => action.payload.username);
});

export default accountUsernameReducer;
