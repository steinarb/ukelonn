import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_JOB_DATE,
    JOB_TABLE_ROW_CLICK,
    REGISTERJOB_RECEIVE,
    CLEAR_REGISTER_JOB_FORM,
    CLEAR_JOB_FORM,
    CLEAR_EDIT_JOB_FORM,
    CLEAR_ACCOUNT,
} from '../actiontypes';
const currentDate = new Date().toISOString();

const transactionDateReducer = createReducer(currentDate, {
    [MODIFY_JOB_DATE]: (state, action) => action.payload,
    [JOB_TABLE_ROW_CLICK]: (state, action) => new Date(action.payload.transactionTime).toISOString(),
    [REGISTERJOB_RECEIVE]: () => new Date().toISOString(),
    [CLEAR_REGISTER_JOB_FORM]: () => new Date().toISOString(),
    [CLEAR_JOB_FORM]: () => new Date().toISOString(),
    [CLEAR_EDIT_JOB_FORM]: () => new Date().toISOString(),
    [CLEAR_ACCOUNT]: () => new Date().toISOString(),
});

export default transactionDateReducer;
