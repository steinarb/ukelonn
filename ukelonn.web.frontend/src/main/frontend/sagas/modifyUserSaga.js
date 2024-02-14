import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    CHANGE_USER_REQUEST,
    CHANGE_USER_RECEIVE,
    CHANGE_USER_FAILURE,
} from '../actiontypes';


function doModifyUser(user) {
    return axios.post('/api/admin/user/modify', user);
}

function* requestReceiveModifyUserSaga(action) {
    try {
        const response = yield call(doModifyUser, action.payload);
        const users = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(CHANGE_USER_RECEIVE(users));
    } catch (error) {
        yield put(CHANGE_USER_FAILURE(error));
    }
}

export default function* modifyUserSaga() {
    yield takeLatest(CHANGE_USER_REQUEST, requestReceiveModifyUserSaga);
}
