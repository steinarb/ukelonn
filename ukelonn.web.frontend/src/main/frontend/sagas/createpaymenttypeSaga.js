import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    CREATE_PAYMENTTYPE_REQUEST,
    CREATE_PAYMENTTYPE_RECEIVE,
    CREATE_PAYMENTTYPE_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestCreatePaymenttypeSaga() {
    yield takeLatest(CREATE_PAYMENTTYPE_REQUEST, receiveCreatePaymenttypeSaga);
}

function doCreatePaymenttype(paymenttype) {
    return axios.post('/ukelonn/api/admin/paymenttype/create', paymenttype);
}

// worker saga
function* receiveCreatePaymenttypeSaga(action) {
    try {
        const response = yield call(doCreatePaymenttype, action.payload);
        const paymenttypes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(CREATE_PAYMENTTYPE_RECEIVE(paymenttypes));
    } catch (error) {
        yield put(CREATE_PAYMENTTYPE_FAILURE(error));
    }
}
