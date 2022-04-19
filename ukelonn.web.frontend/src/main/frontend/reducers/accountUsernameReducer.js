import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    MODIFY_ACCOUNT_USERNAME,
    CLEAR_ACCOUNT,
} from '../actiontypes';
const defaultValue = '';

const accountUsernameReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.username,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.username,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.username,
    [MODIFY_ACCOUNT_USERNAME]: (state, action) => action.payload,
    [CLEAR_ACCOUNT]: () => defaultValue,
});

export default accountUsernameReducer;
