import { takeLatest, put, select } from 'redux-saga/effects';
import {
    SELECT_PAYMENT_TYPE,
    SELECT_PAYMENT_TYPE_FOR_EDIT,
    SELECTED_PAYMENT_TYPE,
    MODIFY_PAYMENT_AMOUNT,
    SAVE_CHANGES_TO_PAYMENT_TYPE_BUTTON_CLICKED,
    MODIFY_PAYMENTTYPE_REQUEST,
    MODIFY_PAYMENTTYPE_RECEIVE,
    CREATE_PAYMENT_TYPE_BUTTON_CLICKED,
    CREATE_PAYMENTTYPE_REQUEST,
    CREATE_PAYMENTTYPE_RECEIVE,
    CLEAR_PAYMENT_TYPE_FORM,
} from '../actiontypes';

function* selectPaymentType(action) {
    const transactionTypeId = parseInt(action.payload);
    if (transactionTypeId === -1) {
        const balance = yield select(state => state.accountBalance);
        yield put(MODIFY_PAYMENT_AMOUNT(balance));
    }
    const paymenttypes = yield select(state => state.paymenttypes);
    const paymenttype = paymenttypes.find(p => p.id === transactionTypeId);
    if (paymenttype && paymenttype.transactionAmount > 0) {
        yield put(MODIFY_PAYMENT_AMOUNT(paymenttype.transactionAmount));
    } else {
        const balance = yield select(state => state.accountBalance);
        yield put(MODIFY_PAYMENT_AMOUNT(balance));
    }
}

function* selectPaymentTypeForEdit(action) {
    const transactionTypeId = action.payload;
    if (transactionTypeId === -1) {
        yield put(CLEAR_PAYMENT_TYPE_FORM());
    }
    const paymenttypes = yield select(state => state.paymenttypes);
    const paymenttype = paymenttypes.find(p => p.id === transactionTypeId);
    if (paymenttype) {
        yield put(SELECTED_PAYMENT_TYPE(paymenttype));
    }
}

function* buildRequestAndSaveModifiedPaymentType() {
    const id = yield select(state => state.transactionTypeId);
    const transactionTypeName = yield select(state => state.transactionTypeName);
    const transactionAmount = yield select(state => state.transactionAmount);
    yield put(MODIFY_PAYMENTTYPE_REQUEST({ id, transactionTypeName, transactionAmount }));
}

function* buildRequestAndSaveCreatedPaymentType() {
    const transactionTypeName = yield select(state => state.transactionTypeName);
    const transactionAmount = yield select(state => state.transactionAmount);
    yield put(CREATE_PAYMENTTYPE_REQUEST({ transactionTypeName, transactionAmount }));
}

function* clearPaymenttypeForm() {
    yield put(CLEAR_PAYMENT_TYPE_FORM());
}

export default function* paymentSaga() {
    yield takeLatest(SELECT_PAYMENT_TYPE, selectPaymentType);
    yield takeLatest(SELECT_PAYMENT_TYPE_FOR_EDIT, selectPaymentTypeForEdit);
    yield takeLatest(SAVE_CHANGES_TO_PAYMENT_TYPE_BUTTON_CLICKED, buildRequestAndSaveModifiedPaymentType);
    yield takeLatest(CREATE_PAYMENT_TYPE_BUTTON_CLICKED, buildRequestAndSaveCreatedPaymentType);
    yield takeLatest(MODIFY_PAYMENTTYPE_RECEIVE, clearPaymenttypeForm);
    yield takeLatest(CREATE_PAYMENTTYPE_RECEIVE, clearPaymenttypeForm);
}
