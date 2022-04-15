import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    MODIFY_ACCOUNT_LASTNAME,
    CLEAR_ACCOUNT,
} from '../actiontypes';
const defaultValue = '';

const accountLastnameReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.lastName,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.lastName,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.lastName,
    [MODIFY_ACCOUNT_LASTNAME]: (state, action) => action.payload,
    [CLEAR_ACCOUNT]: () => defaultValue,
});

export default accountLastnameReducer;
