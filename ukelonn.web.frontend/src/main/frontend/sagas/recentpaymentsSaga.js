import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    RECENTPAYMENTS_REQUEST,
    RECENTPAYMENTS_RECEIVE,
    RECENTPAYMENTS_FAILURE,
    SELECT_PAYMENT_TYPE,
} from '../actiontypes';

// watcher saga
export function* requestRecentPaymentsSaga() {
    yield takeLatest(RECENTPAYMENTS_REQUEST, receiveRecentPaymentsSaga);
}

function doRecentPayments(accountId) {
    return axios.get('/ukelonn/api/payments/' + accountId);
}

// worker saga
function* receiveRecentPaymentsSaga(action) {
    try {
        const response = yield call(doRecentPayments, action.payload);
        const payments = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(RECENTPAYMENTS_RECEIVE(payments));
        if (payments.length) {
            const lastPreviousPaymentType = payments[payments.length - 1].transactionType;
            if (lastPreviousPaymentType) {
                yield put(SELECT_PAYMENT_TYPE(lastPreviousPaymentType.id));
            }
        }
    } catch (error) {
        yield put(RECENTPAYMENTS_FAILURE(error));
    }
}
