import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    ACCOUNTS_REQUEST,
    ACCOUNTS_RECEIVE,
    ACCOUNTS_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestAccountsSaga() {
    yield takeLatest(ACCOUNTS_REQUEST, receiveAccountsSaga);
}

function doAccounts() {
    return axios.get('/ukelonn/api/accounts');
}

// worker saga
function* receiveAccountsSaga(action) {
    try {
        const response = yield call(doAccounts);
        const accounts = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: ACCOUNTS_RECEIVE, accounts: accounts });
    } catch (error) {
        yield put({ type: ACCOUNTS_FAILURE, error });
    }
}
