import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECT_ACCOUNT,
} from '../actiontypes';

const defaultValue = '';

const accountFullnameReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(ACCOUNT_RECEIVE, (state, action) => action.payload.fullName)
        .addCase(REGISTERJOB_RECEIVE, (state, action) => action.payload.fullName)
        .addCase(REGISTERPAYMENT_RECEIVE, (state, action) => action.payload.fullName)
        .addCase(SELECT_ACCOUNT, (state, action) => action.payload.fullName);
});

export default accountFullnameReducer;
