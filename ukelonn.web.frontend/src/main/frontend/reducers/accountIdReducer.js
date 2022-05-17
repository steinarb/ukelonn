import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECT_ACCOUNT,
} from '../actiontypes';
const defaultValue = -1;

const accountIdReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.accountId,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.accountId,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.accountId,
    [SELECT_ACCOUNT]: (state, action) => action.payload.accountId,
});

export default accountIdReducer;
