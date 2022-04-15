import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    MODIFY_ACCOUNT_FIRSTNAME,
    CLEAR_ACCOUNT,
} from '../actiontypes';
const defaultValue = '';

const accountFirstnameReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.firstName,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.firstName,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.firstName,
    [MODIFY_ACCOUNT_FIRSTNAME]: (state, action) => action.payload,
    [CLEAR_ACCOUNT]: () => defaultValue,
});

export default accountFirstnameReducer;
