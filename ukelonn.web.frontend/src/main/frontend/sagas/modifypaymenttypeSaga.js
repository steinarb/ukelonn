import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    MODIFY_PAYMENTTYPE_REQUEST,
    MODIFY_PAYMENTTYPE_RECEIVE,
    MODIFY_PAYMENTTYPE_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestModifyPaymenttypeSaga() {
    yield takeLatest(MODIFY_PAYMENTTYPE_REQUEST, receiveModifyPaymenttypeSaga);
}

function doModifyPaymenttype(paymenttype) {
    return axios.post('/ukelonn/api/admin/paymenttype/modify', paymenttype);
}

// worker saga
function* receiveModifyPaymenttypeSaga(action) {
    try {
        const response = yield call(doModifyPaymenttype, action.transactiontype);
        const paymenttypes = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: MODIFY_PAYMENTTYPE_RECEIVE, paymenttypes });
    } catch (error) {
        yield put({ type: MODIFY_PAYMENTTYPE_FAILURE, error });
    }
}
