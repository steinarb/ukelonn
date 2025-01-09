import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    SELECTED_JOB_TYPE,
    SELECTED_PAYMENT_TYPE,
    SELECTED_PAYMENT_TYPE_FOR_EDIT,
    CLEAR_JOB_FORM,
    CLEAR_JOB_TYPE_FORM,
    CLEAR_JOB_TYPE_CREATE_FORM,
    CLEAR_PAYMENT_TYPE_FORM,
} from '../actiontypes';
import { api } from '../api';
import { isClearTransactionTypeForm } from '../matchers';
import { isUnselected } from '../common/reducers';

const defaultValue = '';

const transactionTypeNameReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(MODIFY_TRANSACTION_TYPE_NAME, (state, action) => action.payload)
        .addCase(SELECTED_JOB_TYPE, (state, action) => isUnselected(action.payload.id) ? defaultValue : action.payload.transactionTypeName)
        .addCase(SELECTED_PAYMENT_TYPE, (state, action) => isUnselected(action.payload.id) ? defaultValue : (action.payload.transactionTypeName || defaultValue))
        .addCase(SELECTED_PAYMENT_TYPE_FOR_EDIT, (state, action) => isUnselected(action.payload.id) ? defaultValue : (action.payload.transactionTypeName || defaultValue))
        .addMatcher(api.endpoints.postJobRegister.matchFulfilled, () => defaultValue)
        .addCase(CLEAR_JOB_FORM, () => defaultValue)
        .addMatcher(isClearTransactionTypeForm, () => defaultValue)
        .addCase(CLEAR_JOB_TYPE_CREATE_FORM, () => defaultValue)
        .addCase(CLEAR_PAYMENT_TYPE_FORM, () => defaultValue);
});

export default transactionTypeNameReducer;
