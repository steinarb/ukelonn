import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECT_ACCOUNT,
} from '../actiontypes';

const defaultValue = 0;

const accountBalanceReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.balance,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.balance,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.balance,
    [SELECT_ACCOUNT]: (state, action) => action.payload.balance,
});

export default accountBalanceReducer;
