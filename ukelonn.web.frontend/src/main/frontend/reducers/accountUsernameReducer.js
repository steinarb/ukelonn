import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECT_ACCOUNT,
} from '../actiontypes';

const defaultValue = '';

const accountUsernameReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.username,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.username,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.username,
    [SELECT_ACCOUNT]: (state, action) => action.payload.username,
});

export default accountUsernameReducer;
