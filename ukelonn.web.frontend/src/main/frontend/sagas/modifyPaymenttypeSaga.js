import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    MODIFY_PAYMENTTYPE_REQUEST,
    MODIFY_PAYMENTTYPE_RECEIVE,
    MODIFY_PAYMENTTYPE_FAILURE,
    CLEAR_PAYMENT_TYPE_FORM,
    SAVE_CHANGES_TO_PAYMENT_TYPE_BUTTON_CLICKED,
} from '../actiontypes';

function doModifyPaymenttype(paymenttype) {
    return axios.post('/ukelonn/api/admin/paymenttype/modify', paymenttype);
}

function* requestReceiveModifyPaymenttypeSaga(action) {
    try {
        const response = yield call(doModifyPaymenttype, action.payload);
        const paymenttypes = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(MODIFY_PAYMENTTYPE_RECEIVE(paymenttypes));
    } catch (error) {
        yield put(MODIFY_PAYMENTTYPE_FAILURE(error));
    }
}

function* clearPaymenttypeForm() {
    yield put(CLEAR_PAYMENT_TYPE_FORM());
}

function* buildRequestAndSavePaymentType() {
    const id = yield select(state => state.transactionTypeId);
    const transactionTypeName = yield select(state => state.transactionTypeName);
    const transactionAmount = yield select(state => state.transactionAmount);
    yield put(MODIFY_PAYMENTTYPE_REQUEST({ id, transactionTypeName, transactionAmount }));
}

export default function* modifyPaymenttypeSaga() {
    yield takeLatest(MODIFY_PAYMENTTYPE_REQUEST, requestReceiveModifyPaymenttypeSaga);
    yield takeLatest(MODIFY_PAYMENTTYPE_RECEIVE, clearPaymenttypeForm);
    yield takeLatest(SAVE_CHANGES_TO_PAYMENT_TYPE_BUTTON_CLICKED, buildRequestAndSavePaymentType);
}
