import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    MODIFY_ACCOUNT_BALANCE,
    CLEAR_ACCOUNT,
} from '../actiontypes';
const defaultValue = 0;

const accountBalanceReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.balance,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.balance,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.balance,
    [MODIFY_ACCOUNT_BALANCE]: (state, action) => action.payload,
    [CLEAR_ACCOUNT]: () => defaultValue,
});

export default accountBalanceReducer;
