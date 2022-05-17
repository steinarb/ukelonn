import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECT_ACCOUNT,
} from '../actiontypes';

const defaultValue = '';

const accountLastnameReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.lastName,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.lastName,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.lastName,
    [SELECT_ACCOUNT]: (state, action) => action.payload.lastName,
});

export default accountLastnameReducer;
