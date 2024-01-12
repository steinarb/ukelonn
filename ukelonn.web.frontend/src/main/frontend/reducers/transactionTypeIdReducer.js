import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_JOB_TYPE,
    SELECT_PAYMENT_TYPE,
    SELECT_PAYMENT_TYPE_FOR_EDIT,
    JOB_TABLE_ROW_CLICK,
    REGISTERJOB_RECEIVE,
    CLEAR_JOB_FORM,
    CLEAR_EDIT_JOB_FORM,
    CLEAR_JOB_TYPE_FORM,
    CLEAR_PAYMENT_TYPE_FORM,
    CLEAR_REGISTER_JOB_FORM,
} from '../actiontypes';
const unselectedId = -1;

const transactionTypeIdReducer = createReducer(unselectedId, builder => {
    builder
        .addCase(SELECT_JOB_TYPE, (state, action) => action.payload)
        .addCase(SELECT_PAYMENT_TYPE, (state, action) => action.payload)
        .addCase(SELECT_PAYMENT_TYPE_FOR_EDIT, (state, action) => action.payload)
        .addCase(JOB_TABLE_ROW_CLICK, (state, action) => parseInt(action.payload.transactionType.id))
        .addCase(REGISTERJOB_RECEIVE, () => unselectedId)
        .addCase(CLEAR_JOB_FORM, () => unselectedId)
        .addCase(CLEAR_EDIT_JOB_FORM, () => unselectedId)
        .addCase(CLEAR_JOB_TYPE_FORM, () => unselectedId)
        .addCase(CLEAR_PAYMENT_TYPE_FORM, () => unselectedId)
        .addCase(CLEAR_REGISTER_JOB_FORM, () => unselectedId);
});

export default transactionTypeIdReducer;
