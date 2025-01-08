import { createReducer } from '@reduxjs/toolkit';
import {
    SELECTED_JOB_TYPE,
    SELECTED_PAYMENT_TYPE,
    SELECTED_PAYMENT_TYPE_FOR_EDIT,
    JOB_TABLE_ROW_CLICK,
    CLEAR_JOB_FORM,
    CLEAR_EDIT_JOB_FORM,
    CLEAR_JOB_TYPE_FORM,
    CLEAR_PAYMENT_TYPE_FORM,
} from '../actiontypes';
import { api } from '../api';
import { isClearTransactionTypeForm } from '../matchers';
const unselectedId = -1;

const transactionTypeIdReducer = createReducer(unselectedId, builder => {
    builder
        .addMatcher(api.endpoints.getPaymenttypes.matchFulfilled, (_, action) => (action.payload.length && action.payload[0].id) || unselectedId)
        .addCase(SELECTED_JOB_TYPE, (state, action) => action.payload.id)
        .addCase(SELECTED_PAYMENT_TYPE, (state, action) => action.payload.id)
        .addCase(SELECTED_PAYMENT_TYPE_FOR_EDIT, (state, action) => action.payload.id)
        .addCase(JOB_TABLE_ROW_CLICK, (state, action) => parseInt(action.payload.transactionType.id))
        .addCase(CLEAR_JOB_FORM, () => unselectedId)
        .addMatcher(isClearTransactionTypeForm, () => unselectedId)
        .addCase(CLEAR_JOB_TYPE_FORM, () => unselectedId)
        .addCase(CLEAR_PAYMENT_TYPE_FORM, () => unselectedId)
        .addMatcher(api.endpoints.postJobtypeModify.matchFulfilled, () => unselectedId)
        .addMatcher(api.endpoints.postJobRegister.matchFulfilled, () => unselectedId);
});

export default transactionTypeIdReducer;
