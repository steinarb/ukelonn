import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    INITIAL_LOGIN_STATE_REQUEST,
    INITIAL_LOGIN_STATE_RECEIVE,
    INITIAL_LOGIN_STATE_FAILURE,
    LOGIN_REQUEST,
    LOGIN_RECEIVE,
    LOGIN_FAILURE,
} from '../actiontypes';
import { emptyLoginResponse } from './constants';

export function* requestInitialLoginStateSaga() {
    yield takeLatest(INITIAL_LOGIN_STATE_REQUEST, receiveInitialLoginStateSaga);
}

function doGetLogin() {
    return axios.get('/ukelonn/api/login');
}

// worker saga
export function* receiveInitialLoginStateSaga() {
    try {
        const response = yield call(doGetLogin);
        const loginResponse = (response.headers['content-type'] == 'application/json') ? response.data : emptyLoginResponse;
        yield put(INITIAL_LOGIN_STATE_RECEIVE(loginResponse));
    } catch (error) {
        yield put(INITIAL_LOGIN_STATE_FAILURE(error));
    }
}

// watcher saga
export function* requestLoginSaga() {
    yield takeLatest(LOGIN_REQUEST, receiveLoginSaga);
}

function doLogin(username, password) {
    return axios.post('/ukelonn/api/login', { username, password });
}

// worker saga
function* receiveLoginSaga(action) {
    try {
        const payload = action.payload || {};
        const response = yield call(doLogin, payload.username, payload.password);
        const loginResponse = (response.headers['content-type'] === 'application/json') ? response.data : emptyLoginResponse;
        yield put(LOGIN_RECEIVE(loginResponse));
    } catch (error) {
        yield put(LOGIN_FAILURE(error));
    }
}
