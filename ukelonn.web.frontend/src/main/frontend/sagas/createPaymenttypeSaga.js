import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    CREATE_PAYMENTTYPE_REQUEST,
    CREATE_PAYMENTTYPE_RECEIVE,
    CREATE_PAYMENTTYPE_FAILURE,
    CLEAR_PAYMENT_TYPE_FORM,
    CREATE_PAYMENT_TYPE_BUTTON_CLICKED,
} from '../actiontypes';

function doCreatePaymenttype(paymenttype) {
    return axios.post('/ukelonn/api/admin/paymenttype/create', paymenttype);
}

function* requestReceiveCreatePaymenttype(action) {
    try {
        const response = yield call(doCreatePaymenttype, action.payload);
        const paymenttypes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(CREATE_PAYMENTTYPE_RECEIVE(paymenttypes));
        yield put(CLEAR_PAYMENT_TYPE_FORM());
    } catch (error) {
        yield put(CREATE_PAYMENTTYPE_FAILURE(error));
    }
}

function* createPaymenttype() {
    const transactionTypeName = yield select(state => state.transactionTypeName);
    const transactionAmount = yield select(state => state.transactionAmount);
    yield put(CREATE_PAYMENTTYPE_REQUEST({ transactionTypeName, transactionAmount }));
}

export default function* createPaymenttypeSaga() {
    yield takeLatest(CREATE_PAYMENTTYPE_REQUEST, requestReceiveCreatePaymenttype);
    yield takeLatest(CREATE_PAYMENT_TYPE_BUTTON_CLICKED, createPaymenttype);
}
