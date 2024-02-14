import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    CHANGE_USER_PASSWORD_REQUEST,
    CHANGE_USER_PASSWORD_RECEIVE,
    CHANGE_USER_PASSWORD_FAILURE,
} from '../actiontypes';

function doChangePassword(passwords) {
    return axios.post('/api/admin/user/password', passwords);
}

function* requestReceiveChangePasswordSaga(action) {
    try {
        const response = yield call(doChangePassword, action.payload);
        const users = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(CHANGE_USER_PASSWORD_RECEIVE(users));
    } catch (error) {
        yield put(CHANGE_USER_PASSWORD_FAILURE(error));
    }
}

export default function* changeUserPasswordSaga() {
    yield takeLatest(CHANGE_USER_PASSWORD_REQUEST, requestReceiveChangePasswordSaga);
}
