import { takeLatest, put, select } from 'redux-saga/effects';
import {
    SELECT_USER,
    MODIFY_USER_USERNAME,
    MODIFY_USER_EMAIL,
    MODIFY_USER_FIRSTNAME,
    MODIFY_USER_LASTNAME,
    REQUEST_ADMIN_STATUS,
    USERS_RECEIVE,
    CHANGE_USER_RECEIVE,
    CREATE_USER_RECEIVE,
    CLEAR_USER,
} from '../actiontypes';

function* selectUser(action) {
    const userid = action.payload;
    if (userid === -1) {
        yield put(CLEAR_USER());
    } else {
        const users = yield select(state => state.users);
        const user = users.find(u => u.userid === userid);
        if (user) {
            const { username } = user;
            if (username) {
                yield put(MODIFY_USER_USERNAME(username));
                yield put(REQUEST_ADMIN_STATUS(username));
            }
            yield put(MODIFY_USER_EMAIL(user.email));
            yield put(MODIFY_USER_FIRSTNAME(user.firstname));
            yield put(MODIFY_USER_LASTNAME(user.lastname));
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
