import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    REGISTERPAYMENT_REQUEST,
    REGISTERPAYMENT_RECEIVE,
    REGISTERPAYMENT_FAILURE,
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


export default function* registerPaymentSaga() {
    yield takeLatest(REGISTERPAYMENT_REQUEST, receiveRegisterPaymentSaga);
}
