import { takeLatest, put, select } from 'redux-saga/effects';
import {
    SELECT_USER,
    SELECTED_USER,
    REQUEST_ADMIN_STATUS,
    USERS_RECEIVE,
    CHANGE_USER_RECEIVE,
    CREATE_USER_RECEIVE,
    CLEAR_USER,
} from '../actiontypes';

function* selectUser(action) {
    const userid = action.payload;
    if (userid === -1) {
        yield put(SELECTED_USER({ userid }));
    } else {
        const users = yield select(state => state.users);
        const user = users.find(u => u.userid === userid);
        if (user) {
            yield put(SELECTED_USER(user));
            yield put(REQUEST_ADMIN_STATUS(user));
        }
    }
}

function* clearUserForm() {
    yield put(CLEAR_USER());
}

export default function* userSaga() {
    yield takeLatest(SELECT_USER, selectUser);
    yield takeLatest(USERS_RECEIVE, clearUserForm);
    yield takeLatest(CHANGE_USER_RECEIVE, clearUserForm);
    yield takeLatest(CREATE_USER_RECEIVE, clearUserForm);
}
