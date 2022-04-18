import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    REGISTERJOB_RECEIVE,
    CLEAR_JOB_FORM,
    CLEAR_JOB_TYPE_FORM,
    CLEAR_JOB_TYPE_CREATE_FORM,
    CLEAR_PAYMENT_TYPE_FORM,
} from '../actiontypes';
const emptyName = '';

const transactionTypeNameReducer = createReducer(emptyName, {
    [MODIFY_TRANSACTION_TYPE_NAME]: (state, action) => action.payload,
    [REGISTERJOB_RECEIVE]: () => emptyName,
    [CLEAR_JOB_FORM]: () => emptyName,
    [CLEAR_JOB_TYPE_FORM]: () => emptyName,
    [CLEAR_JOB_TYPE_CREATE_FORM]: () => emptyName,
    [CLEAR_PAYMENT_TYPE_FORM]: () => emptyName,
});

export default transactionTypeNameReducer;
