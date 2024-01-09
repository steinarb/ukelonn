import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECT_ACCOUNT,
} from '../actiontypes';

const defaultValue = 0;

const accountBalanceReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(ACCOUNT_RECEIVE, (state, action) => action.payload.balance)
        .addCase(REGISTERJOB_RECEIVE, (state, action) => action.payload.balance)
        .addCase(REGISTERPAYMENT_RECEIVE, (state, action) => action.payload.balance)
        .addCase(SELECT_ACCOUNT, (state, action) => action.payload.balance);
});

export default accountBalanceReducer;
