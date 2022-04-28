import { takeLatest, put, select } from 'redux-saga/effects';
import {
    SELECT_USER,
    SELECTED_USER,
    REQUEST_ADMIN_STATUS,
    USERS_RECEIVE,
    CHANGE_USER_RECEIVE,
    CREATE_USER_RECEIVE,
    CHANGE_USER_REQUEST,
    CHANGE_ADMIN_STATUS,
    SAVE_USER_BUTTON_CLICKED,
    CHANGE_PASSWORD_BUTTON_CLICKED,
    CHANGE_USER_PASSWORD_REQUEST,
    CHANGE_USER_PASSWORD_RECEIVE,
    CREATE_USER_BUTTON_CLICKED,
    CREATE_USER_REQUEST,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
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

function* collectAndSaveModifiedUser() {
    const userid = yield select(state => state.userid);
    const username = yield select(state => state.userUsername);
    const email = yield select(state => state.userEmail);
    const firstname = yield select(state => state.userFirstname);
    const lastname = yield select(state => state.userLastname);
    const user = {
        userid,
        username,
        email,
        firstname,
        lastname,
    };
    yield put(CHANGE_USER_REQUEST(user));
    const administrator = yield select(state => state.userIsAdministrator);
    yield put(CHANGE_ADMIN_STATUS({ user, administrator }));
}

function* collectDataAndSaveUpdatedPassword() {
    const userid = yield select(state => state.userid);
    const user = { userid };
    const password1 = yield select(state => state.password1);
    const password2 = yield select(state => state.password2);
    const passwordsNotIdentical = yield select(state => state.passwordsNotIdentical);
    yield put(CHANGE_USER_PASSWORD_REQUEST({ user, password1, password2, passwordsNotIdentical }));
}

function* saveCreatedUser() {
    const username = yield select(state => state.userUsername);
    const email = yield select(state => state.userEmail);
    const firstname = yield select(state => state.userFirstname);
    const lastname = yield select(state => state.userLastname);
    const user = {
        username,
        email,
        firstname,
        lastname,
    };
    const password1 = yield select(state => state.password1);
    const password2 = yield select(state => state.password2);
    const passwordsNotIdentical = yield select(state => state.passwordsNotIdentical);
    const userAndPasswords = {
        user,
        password1,
        password2,
        passwordsNotIdentical,
    };
    yield put(CREATE_USER_REQUEST(userAndPasswords));
    const administrator = yield select(state => state.userIsAdministrator);
    yield put(CHANGE_ADMIN_STATUS({ user, administrator }));
}

function* clearUserForm() {
    yield put(CLEAR_USER());
}

function* clearUserAndPasswordForms() {
    yield put(CLEAR_USER_AND_PASSWORDS());
}

export default function* userSaga() {
    yield takeLatest(SELECT_USER, selectUser);
    yield takeLatest(USERS_RECEIVE, clearUserForm);
    yield takeLatest(SAVE_USER_BUTTON_CLICKED, collectAndSaveModifiedUser);
    yield takeLatest(CHANGE_PASSWORD_BUTTON_CLICKED, collectDataAndSaveUpdatedPassword);
    yield takeLatest(CREATE_USER_BUTTON_CLICKED, saveCreatedUser);
    yield takeLatest(CHANGE_USER_RECEIVE, clearUserForm);
    yield takeLatest(CHANGE_USER_PASSWORD_RECEIVE, clearUserAndPasswordForms);
    yield takeLatest(CREATE_USER_RECEIVE, clearUserAndPasswordForms);
}
