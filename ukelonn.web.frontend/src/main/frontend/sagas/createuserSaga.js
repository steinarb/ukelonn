import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    CREATE_USER_REQUEST,
    CREATE_USER_RECEIVE,
    CREATE_USER_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestCreateUserSaga() {
    yield takeLatest(CREATE_USER_REQUEST, receiveCreateUserSaga);
}

function doCreateUser(passwords) {
    delete passwords.user.fullname;
    return axios.post('/ukelonn/api/admin/user/create', passwords);
}

// worker saga
function* receiveCreateUserSaga(action) {
    try {
        const payload = action.payload || {};
        const passwords = {...payload.passwords, user: {...payload.user}};
        const response = yield call(doCreateUser, passwords);
        const users = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(CREATE_USER_RECEIVE(users));
    } catch (error) {
        yield put(CREATE_USER_FAILURE(error));
    }
}
