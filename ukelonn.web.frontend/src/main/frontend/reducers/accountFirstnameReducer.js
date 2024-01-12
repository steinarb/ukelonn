import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECT_ACCOUNT,
} from '../actiontypes';

const defaultValue = '';

const accountFirstnameReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(ACCOUNT_RECEIVE, (state, action) => action.payload.firstName)
        .addCase(REGISTERJOB_RECEIVE, (state, action) => action.payload.firstName)
        .addCase(REGISTERPAYMENT_RECEIVE, (state, action) => action.payload.firstName)
        .addCase(SELECT_ACCOUNT, (state, action) => action.payload.firstName);
});

export default accountFirstnameReducer;
