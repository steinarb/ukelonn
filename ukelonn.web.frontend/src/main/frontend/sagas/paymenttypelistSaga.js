import { takeLatest, call, put, fork } from 'redux-saga/effects';
import axios from 'axios';
import {
    PAYMENTTYPELIST_REQUEST,
    PAYMENTTYPELIST_RECEIVE,
    PAYMENTTYPELIST_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestPaymenttypeListSaga() {
    yield takeLatest(PAYMENTTYPELIST_REQUEST, receivePaymenttypeListSaga);
}

function doPaymenttypeList() {
    return axios.get('/ukelonn/api/paymenttypes');
}

// worker saga
function* receivePaymenttypeListSaga(action) {
    try {
        const response = yield call(doPaymenttypeList);
        const paymenttypes = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(PAYMENTTYPELIST_RECEIVE(paymenttypes));
    } catch (error) {
        yield put(PAYMENTTYPELIST_FAILURE(error));
    }
}
