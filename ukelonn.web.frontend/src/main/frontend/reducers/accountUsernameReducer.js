import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECT_ACCOUNT,
} from '../actiontypes';

const defaultValue = '';

const accountUsernameReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(ACCOUNT_RECEIVE, (state, action) => action.payload.username)
        .addCase(REGISTERJOB_RECEIVE, (state, action) => action.payload.username)
        .addCase(REGISTERPAYMENT_RECEIVE, (state, action) => action.payload.username)
        .addCase(SELECT_ACCOUNT, (state, action) => action.payload.username);
});

export default accountUsernameReducer;
