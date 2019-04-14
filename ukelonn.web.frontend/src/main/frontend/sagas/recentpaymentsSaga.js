import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    RECENTPAYMENTS_REQUEST,
    RECENTPAYMENTS_RECEIVE,
    RECENTPAYMENTS_FAILURE,
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
        const response = yield call(doRecentPayments, action.accountId);
        const payments = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: RECENTPAYMENTS_RECEIVE, payments: payments });
    } catch (error) {
        yield put({ type: RECENTPAYMENTS_FAILURE, error });
    }
}
