import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    PAYMENTTYPES_REQUEST,
    PAYMENTTYPES_RECEIVE,
    PAYMENTTYPES_FAILURE,
    SELECT_PAYMENT_TYPE,
} from '../actiontypes';

export default function* paymenttypesSaga() {
    yield takeLatest(PAYMENTTYPES_REQUEST, receivePaymenttypesSaga);
}

function* receivePaymenttypesSaga() {
    try {
        const response = yield call(doPaymenttypes);
        const paymenttypes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(PAYMENTTYPES_RECEIVE(paymenttypes));
        if (paymenttypes.length) {
            yield put(SELECT_PAYMENT_TYPE(paymenttypes[0].id));
        }
    } catch (error) {
        yield put(PAYMENTTYPES_FAILURE(error));
    }
}

function doPaymenttypes() {
    return axios.get('/api/paymenttypes');
}
