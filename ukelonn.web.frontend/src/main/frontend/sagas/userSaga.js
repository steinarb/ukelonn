import { takeLatest, put, select } from 'redux-saga/effects';
import {
    SELECT_USER,
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

export default function* userSaga() {
    yield takeLatest(SELECT_USER, queryAdminStatusForSelectedUser);
    yield takeLatest(USERS_RECEIVE, clearUserForm);
    yield takeLatest(SAVE_USER_BUTTON_CLICKED, collectAndSaveModifiedUser);
    yield takeLatest(CHANGE_PASSWORD_BUTTON_CLICKED, collectDataAndSaveUpdatedPassword);
    yield takeLatest(CREATE_USER_BUTTON_CLICKED, saveCreatedUser);
    yield takeLatest(CHANGE_USER_RECEIVE, clearUserForm);
    yield takeLatest(CHANGE_USER_PASSWORD_RECEIVE, clearUserAndPasswordForms);
    yield takeLatest(CREATE_USER_RECEIVE, clearUserAndPasswordForms);
}

function* queryAdminStatusForSelectedUser(action) {
    if (action.payload.userid !== -1) {
        yield put(REQUEST_ADMIN_STATUS(action.payload));
    }
}

function* collectAndSaveModifiedUser() {
    const user = yield select(state => ({
        userid: state.userid,
        username: state.userUsername,
        email: state.userEmail,
        firstname: state.userFirstname,
        lastname: state.userLastname,
    }));
    yield put(CHANGE_USER_REQUEST(user));
    const administrator = yield select(state => state.userIsAdministrator);
    yield put(CHANGE_ADMIN_STATUS({ user, administrator }));
}

function* collectDataAndSaveUpdatedPassword() {
    const request = yield select(state => ({
        user: { userid: state.userid },
        password1: state.password1,
        password2: state.password2,
        passwordsNotIdentical: state.passwordsNotIdentical,
    }));
    yield put(CHANGE_USER_PASSWORD_REQUEST(request));
}

function* saveCreatedUser() {
    const userAndPasswords = yield select(state => ({
        user: {
            username: state.userUsername,
            email: state.userEmail,
            firstname: state.userFirstname,
            lastname: state.userLastname,
        },
        password1: state.password1,
        password2: state.password2,
        passwordsNotIdentical: state.passwordsNotIdentical,
    }));
    yield put(CREATE_USER_REQUEST(userAndPasswords));
    const administrator = yield select(state => state.userIsAdministrator);
    yield put(CHANGE_ADMIN_STATUS({ user: userAndPasswords.user, administrator }));
}

function* clearUserForm() {
    yield put(CLEAR_USER());
}

function* clearUserAndPasswordForms() {
    yield put(CLEAR_USER_AND_PASSWORDS());
}
