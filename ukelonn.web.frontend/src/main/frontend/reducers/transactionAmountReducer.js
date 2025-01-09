import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_JOB_AMOUNT,
    MODIFY_PAYMENT_AMOUNT,
    SELECT_ACCOUNT,
    SELECTED_JOB_TYPE,
    SELECTED_PAYMENT_TYPE,
    SELECTED_PAYMENT_TYPE_FOR_EDIT,
    JOB_TABLE_ROW_CLICK,
    CLEAR_JOB_FORM,
    CLEAR_EDIT_JOB_FORM,
    CLEAR_JOB_TYPE_FORM,
    CLEAR_JOB_TYPE_CREATE_FORM,
    CLEAR_PAYMENT_TYPE_FORM,
} from '../actiontypes';
import { api } from '../api';
import { isClearTransactionTypeForm } from '../matchers';
import { isUnselected } from '../common/reducers';

const defaultValue = 0;

const transactionAmountReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(MODIFY_JOB_AMOUNT, (state, action) => action.payload)
        .addCase(MODIFY_PAYMENT_AMOUNT, (state, action) => action.payload)
        .addCase(SELECT_ACCOUNT, (state, action) => action.payload.balance)
        .addCase(SELECTED_JOB_TYPE, (state, action) => isUnselected(action.payload.id) ? defaultValue : action.payload.transactionAmount)
        .addCase(SELECTED_PAYMENT_TYPE, (state, action) => isUnselected(action.payload.id) ? defaultValue : (action.payload.transactionAmount || defaultValue))
        .addCase(SELECTED_PAYMENT_TYPE_FOR_EDIT, (state, action) => isUnselected(action.payload.id) ? defaultValue : (action.payload.transactionAmount || defaultValue))
        .addMatcher(api.endpoints.postPaymentRegister.matchFulfilled, (state, action) => action.payload.balance)
        .addCase(JOB_TABLE_ROW_CLICK, (state, action) => parseInt(action.payload.transactionAmount))
        .addMatcher(api.endpoints.postJobRegister.matchFulfilled, () => defaultValue)
        .addCase(CLEAR_EDIT_JOB_FORM, () => defaultValue)
        .addCase(CLEAR_JOB_FORM, () => defaultValue)
        .addMatcher(isClearTransactionTypeForm, () => defaultValue)
        .addCase(CLEAR_JOB_TYPE_CREATE_FORM, () => defaultValue)
        .addCase(CLEAR_PAYMENT_TYPE_FORM, () => defaultValue);
});

export default transactionAmountReducer;
