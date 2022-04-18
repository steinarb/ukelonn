import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    CHANGE_USER_PASSWORD_REQUEST,
    CHANGE_USER_PASSWORD_RECEIVE,
    CHANGE_USER_PASSWORD_FAILURE,
    CHANGE_PASSWORD_BUTTON_CLICKED,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';

function doChangePassword(passwords) {
    return axios.post('/ukelonn/api/admin/user/password', passwords);
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

function* collectDataAndSaveUpdatedPassword() {
    const userid = yield select(state => state.userid);
    const user = { userid };
    const password1 = yield select(state => state.password1);
    const password2 = yield select(state => state.password2);
    const passwordsNotIdentical = yield select(state => state.passwordsNotIdentical);
    yield put(CHANGE_USER_PASSWORD_REQUEST({ user, password1, password2, passwordsNotIdentical }));
}

function* clearFormFields() {
    yield put(CLEAR_USER_AND_PASSWORDS());
}

export default function* changeUserPasswordSaga() {
    yield takeLatest(CHANGE_USER_PASSWORD_REQUEST, requestReceiveChangePasswordSaga);
    yield takeLatest(CHANGE_PASSWORD_BUTTON_CLICKED, collectDataAndSaveUpdatedPassword);
    yield takeLatest(CHANGE_USER_PASSWORD_RECEIVE, clearFormFields);
}
