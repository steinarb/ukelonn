import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECTED_ACCOUNT,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = '';

const accountFirstnameReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.firstName,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.firstName,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.firstName,
    [SELECTED_ACCOUNT]: (state, action) => isUnselected(action.payload.accountId) ? defaultValue : action.payload.firstName,
});

export default accountFirstnameReducer;
