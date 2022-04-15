import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    CREATE_USER_REQUEST,
    CREATE_USER_RECEIVE,
    CREATE_USER_FAILURE,
    CREATE_USER_BUTTON_CLICKED,
    CHANGE_ADMIN_STATUS,
    CLEAR_USER_AND_PASSWORDS,
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

function* clearUserAndPasswordForms() {
    yield put(CLEAR_USER_AND_PASSWORDS());
}

export default function* createUserSaga() {
    yield takeLatest(CREATE_USER_REQUEST, requestReceiveCreateUserSaga);
    yield takeLatest(CREATE_USER_BUTTON_CLICKED, saveCreatedUser);
    yield takeLatest(CREATE_USER_RECEIVE, clearUserAndPasswordForms);
}
