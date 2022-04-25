import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECTED_ACCOUNT,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = 0;

const accountBalanceReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.balance,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.balance,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.balance,
    [SELECTED_ACCOUNT]: (state, action) => isUnselected(action.payload.accountId) ? defaultValue : action.payload.balance,
});

export default accountBalanceReducer;
