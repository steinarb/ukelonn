import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    SELECTED_JOB_TYPE,
    SELECTED_PAYMENT_TYPE,
    REGISTERJOB_RECEIVE,
    CLEAR_JOB_FORM,
    CLEAR_JOB_TYPE_FORM,
    CLEAR_JOB_TYPE_CREATE_FORM,
    CLEAR_PAYMENT_TYPE_FORM,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = '';

const transactionTypeNameReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(MODIFY_TRANSACTION_TYPE_NAME, (state, action) => action.payload)
        .addCase(SELECTED_JOB_TYPE, (state, action) => isUnselected(action.payload.accountId) ? defaultValue : action.payload.transactionTypeName)
        .addCase(SELECTED_PAYMENT_TYPE, (state, action) => isUnselected(action.payload.accountId) ? defaultValue : action.payload.transactionTypeName)
        .addCase(REGISTERJOB_RECEIVE, () => defaultValue)
        .addCase(CLEAR_JOB_FORM, () => defaultValue)
        .addCase(CLEAR_JOB_TYPE_FORM, () => defaultValue)
        .addCase(CLEAR_JOB_TYPE_CREATE_FORM, () => defaultValue)
        .addCase(CLEAR_PAYMENT_TYPE_FORM, () => defaultValue);
});

export default transactionTypeNameReducer;
