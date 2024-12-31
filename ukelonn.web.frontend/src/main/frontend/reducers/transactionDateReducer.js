import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_JOB_DATE,
    JOB_TABLE_ROW_CLICK,
    CLEAR_JOB_FORM,
    CLEAR_EDIT_JOB_FORM,
} from '../actiontypes';
import { api } from '../api';
const currentDate = new Date().toISOString();

const transactionDateReducer = createReducer(currentDate, builder => {
    builder
        .addCase(MODIFY_JOB_DATE, (state, action) => action.payload + 'T' + state.split('T')[1])
        .addCase(JOB_TABLE_ROW_CLICK, (state, action) => new Date(action.payload.transactionTime).toISOString())
        .addMatcher(api.endpoints.postJobRegister.matchFulfilled, () => new Date().toISOString())
        .addCase(CLEAR_JOB_FORM, () => new Date().toISOString())
        .addCase(CLEAR_EDIT_JOB_FORM, () => new Date().toISOString());
});

export default transactionDateReducer;
