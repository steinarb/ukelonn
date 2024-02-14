import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    ACCOUNTS_REQUEST,
    ACCOUNTS_RECEIVE,
    ACCOUNTS_FAILURE,
} from '../actiontypes';

function doAccounts() {
    return axios.get('/api/accounts');
}

function* requestReceiveAccountsSaga() {
    try {
        const response = yield call(doAccounts);
        const accounts = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(ACCOUNTS_RECEIVE(accounts));
    } catch (error) {
        yield put(ACCOUNTS_FAILURE(error));
    }
}

export default function* accountsSaga() {
    yield takeLatest(ACCOUNTS_REQUEST, requestReceiveAccountsSaga);
}
