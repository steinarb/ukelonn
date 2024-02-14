import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    LOGOUT_REQUEST,
    LOGOUT_RECEIVE,
    LOGOUT_FAILURE,
    RELOAD_WEB_PAGE,
} from '../actiontypes';
import { emptyLoginResponse } from './constants';

function doLogout() {
    return axios.post('/api/logout', {});
}

function* receiveLogoutSaga() {
    try {
        const response = yield call(doLogout);
        const loginResponse = (response.headers['content-type'] == 'application/json') ? response.data : emptyLoginResponse;
        yield put(LOGOUT_RECEIVE(loginResponse));
    } catch (error) {
        yield put(LOGOUT_FAILURE(error));
    }
}

function* reloadPage(action) {
    if (!action.payload.username) {
        yield put(RELOAD_WEB_PAGE());
    }
}

export default function* logoutSaga() {
    yield takeLatest(LOGOUT_REQUEST, receiveLogoutSaga);
    yield takeLatest(LOGOUT_RECEIVE, reloadPage);
}
