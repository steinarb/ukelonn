import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    CREATE_USER_REQUEST,
    CREATE_USER_RECEIVE,
    CREATE_USER_FAILURE,
} from '../actiontypes';

function doCreateUser(passwords) {
    delete passwords.user.fullname;
    return axios.post('/ukelonn/api/admin/user/create', passwords);
}

function* requestReceiveCreateUserSaga(action) {
    try {
        const response = yield call(doCreateUser, action.payload);
        const users = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(CREATE_USER_RECEIVE(users));
    } catch (error) {
        yield put(CREATE_USER_FAILURE(error));
    }
}

export default function* createUserSaga() {
    yield takeLatest(CREATE_USER_REQUEST, requestReceiveCreateUserSaga);
}
