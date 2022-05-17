import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECT_ACCOUNT,
} from '../actiontypes';

const defaultValue = '';

const accountFirstnameReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.firstName,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.firstName,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.firstName,
    [SELECT_ACCOUNT]: (state, action) => action.payload.firstName,
});

export default accountFirstnameReducer;
