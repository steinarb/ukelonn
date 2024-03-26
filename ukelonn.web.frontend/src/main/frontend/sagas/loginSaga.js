import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    INITIAL_LOGIN_STATE_REQUEST,
    INITIAL_LOGIN_STATE_RECEIVE,
    INITIAL_LOGIN_STATE_FAILURE,
    CHECK_LOGIN_STATE_REQUEST,
    CHECK_LOGIN_STATE_RECEIVE,
    CHECK_LOGIN_STATE_FAILURE,
    LOGIN_REQUEST,
    LOGIN_RECEIVE,
    LOGIN_FAILURE,
    ACCOUNT_REQUEST,
    START_NOTIFICATION_LISTENING,
} from '../actiontypes';
import { emptyLoginResponse } from './constants';

export default function* loginSaga() {
    yield takeLatest(INITIAL_LOGIN_STATE_REQUEST, receiveInitialLoginStateSaga);
    yield takeLatest(CHECK_LOGIN_STATE_REQUEST, receiveCheckLoginStateSaga);
    yield takeLatest(LOGIN_REQUEST, receiveLoginSaga);
}

function* receiveInitialLoginStateSaga() {
    try {
        const response = yield call(doGetLogin);
        const loginResponse = (response.headers['content-type'] == 'application/json') ? response.data : emptyLoginResponse;
        yield put(INITIAL_LOGIN_STATE_RECEIVE(loginResponse));
        const { roles } = loginResponse;
        if (roles.indexOf('ukelonnadmin') === -1) {
            yield put(ACCOUNT_REQUEST(loginResponse.username));
            yield put(START_NOTIFICATION_LISTENING(loginResponse.username));
        }
    } catch (error) {
        yield put(INITIAL_LOGIN_STATE_FAILURE(error));
    }
}

function* receiveCheckLoginStateSaga() {
    try {
        const response = yield call(doGetLogin);
        const loginResponse = (response.headers['content-type'] == 'application/json') ? response.data : emptyLoginResponse;
        yield put(CHECK_LOGIN_STATE_RECEIVE(loginResponse));
    } catch (error) {
        yield put(CHECK_LOGIN_STATE_FAILURE(error));
    }
}

function doGetLogin() {
    return axios.get('/api/login');
}

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

function doLogin(username, password) {
    return axios.post('/api/login', { username, password });
}
