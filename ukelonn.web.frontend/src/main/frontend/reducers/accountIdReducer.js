import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECT_ACCOUNT,
} from '../actiontypes';
const defaultValue = -1;

const accountIdReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(ACCOUNT_RECEIVE, (state, action) => action.payload.accountId)
        .addCase(REGISTERJOB_RECEIVE, (state, action) => action.payload.accountId)
        .addCase(REGISTERPAYMENT_RECEIVE, (state, action) => action.payload.accountId)
        .addCase(SELECT_ACCOUNT, (state, action) => action.payload.accountId);
});

export default accountIdReducer;
