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

const accountUsernameReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.username,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.username,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.username,
    [SELECTED_ACCOUNT]: (state, action) => isUnselected(action.payload.accountId) ? defaultValue : action.payload.username,
    [CLEAR_ACCOUNT]: () => defaultValue,
});

export default accountUsernameReducer;
