import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    MODIFY_ACCOUNT_FULLNAME,
    CLEAR_ACCOUNT,
} from '../actiontypes';
const defaultValue = '';

const accountFullnameReducer = createReducer(defaultValue, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.fullName,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.fullName,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.fullName,
    [MODIFY_ACCOUNT_FULLNAME]: (state, action) => action.payload,
    [CLEAR_ACCOUNT]: () => defaultValue,
});

export default accountFullnameReducer;
