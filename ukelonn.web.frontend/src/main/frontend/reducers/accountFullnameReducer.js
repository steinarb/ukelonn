import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECTED_ACCOUNT,
    CLEAR_ACCOUNT,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = '';

const accountFullnameReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.fullName,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.fullName,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.fullName,
    [SELECTED_ACCOUNT]: (state, action) => isUnselected(action.payload.accountId) ? defaultValue : action.payload.fullName,
    [CLEAR_ACCOUNT]: () => defaultValue,
});

export default accountFullnameReducer;
