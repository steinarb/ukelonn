import { takeLatest, call, put, fork, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    REGISTERPAYMENT_REQUEST,
    REGISTERPAYMENT_RECEIVE,
    REGISTERPAYMENT_FAILURE,
    EARNINGS_SUM_OVER_YEAR_REQUEST,
    EARNINGS_SUM_OVER_MONTH_REQUEST,
} from '../actiontypes';
import { emptyAccount } from '../constants';

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
        const { transactionTypeId, transactionAmount, account } = action.payload;
        const response = yield call(doRegisterPayment, { transactionTypeId, transactionAmount, account });
        const updatedAccount = (response.headers['content-type'] === 'application/json') ? response.data : emptyAccount;
        const paymenttypes = yield select(state => state.paymenttypes);
        const paymenttype = paymenttypes.find(pt => pt.id === transactionTypeId);
        doNotifyPaymentdone(action.payload, paymenttype);
        yield put(REGISTERPAYMENT_RECEIVE(updatedAccount));
    } catch (error) {
        yield put(REGISTERPAYMENT_FAILURE(error));
    }
}
