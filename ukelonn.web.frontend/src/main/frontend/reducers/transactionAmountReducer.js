import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_JOB_AMOUNT,
    MODIFY_PAYMENT_AMOUNT,
    SELECTED_ACCOUNT,
    SELECTED_JOB_TYPE,
    SELECTED_PAYMENT_TYPE,
    REGISTERPAYMENT_RECEIVE,
    JOB_TABLE_ROW_CLICK,
    CLEAR_REGISTER_JOB_FORM,
    CLEAR_JOB_FORM,
    CLEAR_EDIT_JOB_FORM,
    CLEAR_JOB_TYPE_FORM,
    CLEAR_JOB_TYPE_CREATE_FORM,
    CLEAR_PAYMENT_TYPE_FORM,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = 0;

const transactionAmountReducer = createReducer(defaultValue, {
    [MODIFY_JOB_AMOUNT]: (state, action) => action.payload,
    [MODIFY_PAYMENT_AMOUNT]: (state, action) => action.payload,
    [SELECTED_ACCOUNT]: (state, action) => isUnselected(action.payload.accountId) ? defaultValue : action.payload.balance,
    [SELECTED_JOB_TYPE]: (state, action) => isUnselected(action.payload.accountId) ? defaultValue : action.payload.transactionAmount,
    [SELECTED_PAYMENT_TYPE]: (state, action) => isUnselected(action.payload.accountId) ? defaultValue : action.payload.transactionAmount,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.balance,
    [JOB_TABLE_ROW_CLICK]: (state, action) => parseInt(action.payload.transactionAmount),
    [CLEAR_REGISTER_JOB_FORM]: () => defaultValue,
    [CLEAR_EDIT_JOB_FORM]: () => defaultValue,
    [CLEAR_JOB_FORM]: () => defaultValue,
    [CLEAR_JOB_TYPE_FORM]: () => defaultValue,
    [CLEAR_JOB_TYPE_CREATE_FORM]: () => defaultValue,
    [CLEAR_PAYMENT_TYPE_FORM]: () => defaultValue,
});

export default transactionAmountReducer;
