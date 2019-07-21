import { takeLatest, call, put, fork } from 'redux-saga/effects';
import axios from 'axios';
import {
    REGISTERPAYMENT_REQUEST,
    REGISTERPAYMENT_RECEIVE,
    REGISTERPAYMENT_FAILURE,
} from '../actiontypes';
import { emptyAccount } from './constants';

// watcher saga
export function* requestRegisterPaymentSaga() {
    yield takeLatest(REGISTERPAYMENT_REQUEST, receiveRegisterPaymentSaga);
}

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

// worker saga
function* receiveRegisterPaymentSaga(action) {
    try {
        const payload = action.payload || {};
        const response = yield call(doRegisterPayment, payload.payment);
        const account = (response.headers['content-type'] === 'application/json') ? response.data : emptyAccount;
        doNotifyPaymentdone(payload.payment, payload.paymenttype);
        yield put(REGISTERPAYMENT_RECEIVE(account));
    } catch (error) {
        yield put(REGISTERPAYMENT_FAILURE(error));
    }
}
