import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECT_ACCOUNT,
} from '../actiontypes';

const defaultValue = '';

const accountFullnameReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.fullName,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.fullName,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.fullName,
    [SELECT_ACCOUNT]: (state, action) => action.payload.fullName,
});

export default accountFullnameReducer;
