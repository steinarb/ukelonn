import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECT_ACCOUNT,
} from '../actiontypes';

const defaultValue = '';

const accountLastnameReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(ACCOUNT_RECEIVE, (state, action) => action.payload.lastName)
        .addCase(REGISTERJOB_RECEIVE, (state, action) => action.payload.lastName)
        .addCase(REGISTERPAYMENT_RECEIVE, (state, action) => action.payload.lastName)
        .addCase(SELECT_ACCOUNT, (state, action) => action.payload.lastName);
});

export default accountLastnameReducer;
