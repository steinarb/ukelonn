import { takeLatest, call, put, fork } from 'redux-saga/effects';
import axios from 'axios';
import {
    MODIFY_USER_PASSWORD_REQUEST,
    MODIFY_USER_PASSWORD_RECEIVE,
    MODIFY_USER_PASSWORD_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestChangePasswordSaga() {
    yield takeLatest(MODIFY_USER_PASSWORD_REQUEST, receiveChangePasswordSaga);
}

function doChangePassword(passwords) {
    delete passwords.user.fullname;
    return axios.post('/ukelonn/api/admin/user/password', passwords);
}

// worker saga
function* receiveChangePasswordSaga(action) {
    try {
        const payload = action.payload || {};
        const passwords = {...payload.passwords, user: {...payload.user}};
        const response = yield call(doChangePassword, passwords);
        const users = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(MODIFY_USER_PASSWORD_RECEIVE(users));
    } catch (error) {
        yield put(MODIFY_USER_PASSWORD_FAILURE(error));
    }
}
