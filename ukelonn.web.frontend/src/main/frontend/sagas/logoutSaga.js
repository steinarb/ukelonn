import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    LOGOUT_REQUEST,
    LOGOUT_RECEIVE,
    LOGOUT_FAILURE,
    CLEAR_ACCOUNT,
    CLEAR_JOB_FORM,
} from '../actiontypes';
import { emptyLoginResponse } from './constants';

// watcher saga
export function* requestLogoutSaga() {
    yield takeLatest(LOGOUT_REQUEST, receiveLogoutSaga);
}

function doLogout() {
    return axios.post('/ukelonn/api/logout', {});
}

// worker saga
function* receiveLogoutSaga() {
    try {
        const response = yield call(doLogout);
        const loginResponse = (response.headers['content-type'] == 'application/json') ? response.data : emptyLoginResponse;
        yield put(LOGOUT_RECEIVE(loginResponse));
        yield put(CLEAR_ACCOUNT());
        yield put(CLEAR_JOB_FORM());
    } catch (error) {
        yield put(LOGOUT_FAILURE(error));
    }
}
