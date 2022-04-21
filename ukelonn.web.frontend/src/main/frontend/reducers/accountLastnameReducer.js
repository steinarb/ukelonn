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

const accountLastnameReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.lastName,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.lastName,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.lastName,
    [SELECTED_ACCOUNT]: (state, action) => isUnselected(action.payload.accountId) ? defaultValue : action.payload.lastName,
    [CLEAR_ACCOUNT]: () => defaultValue,
});

export default accountLastnameReducer;
