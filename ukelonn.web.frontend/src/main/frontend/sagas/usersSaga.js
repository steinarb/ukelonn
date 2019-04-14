import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    USERS_REQUEST,
    USERS_RECEIVE,
    USERS_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestUsersSaga() {
    yield takeLatest(USERS_REQUEST, receiveUsersSaga);
}

function doUsers() {
    return axios.get('/ukelonn/api/users');
}

// worker saga
function* receiveUsersSaga(action) {
    try {
        const response = yield call(doUsers);
        const users = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: USERS_RECEIVE, users: users });
    } catch (error) {
        yield put({ type: USERS_FAILURE, error });
    }
}
