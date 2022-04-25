import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    REGISTERPAYMENT_REQUEST,
    REGISTERPAYMENT_RECEIVE,
    REGISTERPAYMENT_FAILURE,
    REGISTER_PAYMENT_BUTTON_CLICKED,
} from '../actiontypes';
import { emptyAccount } from '../constants';

function doRegisterPayment(payment) {
    return axios.post('/ukelonn/api/registerpayment', payment);
}

function doNotifyPaymentdone(payment, paymenttype) {
    const notification = {
        title: 'Ukelønn',
        message: payment.transactionAmount + ' kroner ' + paymenttype.transactionTypeName,
    };
    return axios.post('/ukelonn/api/notificationto/' + payment.account.username, notification);
}

function* receiveRegisterPaymentSaga(action) {
    try {
        const { transactionTypeId, transactionAmount, account } = action.payload;
        const response = yield call(doRegisterPayment, { transactionTypeId, transactionAmount, account });
        const updatedAccount = (response.headers['content-type'] === 'application/json') ? response.data : emptyAccount;
        yield put(REGISTERPAYMENT_RECEIVE(updatedAccount));
        const paymenttypes = yield select(state => state.paymenttypes);
        const paymenttype = paymenttypes.find(pt => pt.id === transactionTypeId);
        doNotifyPaymentdone(action.payload, paymenttype);
    } catch (error) {
        yield put(REGISTERPAYMENT_FAILURE(error));
    }
}

function* buildRequestAndRegisterPayment() {
    const accountId = yield select(state => state.accountId);
    const username = yield select(state => state.accountUsername);
    const account = { accountId, username };
    const transactionTypeId = yield select(state => state.transactionTypeId);
    const transactionAmount = yield select(state => state.transactionAmount);
    yield put(REGISTERPAYMENT_REQUEST({ account, transactionTypeId, transactionAmount }));
}

export default function* registerPaymentSaga() {
    yield takeLatest(REGISTERPAYMENT_REQUEST, receiveRegisterPaymentSaga);
    yield takeLatest(REGISTER_PAYMENT_BUTTON_CLICKED, buildRequestAndRegisterPayment);
}
