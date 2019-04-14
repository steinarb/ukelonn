import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    PAYMENTTYPES_REQUEST,
    PAYMENTTYPES_RECEIVE,
    PAYMENTTYPES_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestPaymenttypesSaga() {
    yield takeLatest(PAYMENTTYPES_REQUEST, receivePaymenttypesSaga);
}

function doPaymenttypes() {
    return axios.get('/ukelonn/api/paymenttypes');
}

// worker saga
function* receivePaymenttypesSaga(action) {
    try {
        const response = yield call(doPaymenttypes);
        const paymenttypes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(PAYMENTTYPES_RECEIVE(paymenttypes));
    } catch (error) {
        yield put(PAYMENTTYPES_FAILURE(error));
    }
}
