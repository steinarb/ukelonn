import { takeLatest, put, select } from 'redux-saga/effects';
import {
    MODIFY_PASSWORD1,
    MODIFY_PASSWORD2,
    MODIFY_PASSWORDS_NOT_IDENTICAL,
} from '../actiontypes';

function* comparePasswords() {
    const password1 = yield select(state => state.password1);
    const password2 = yield select(state => state.password2);
    if (!password2) {
        // if second password is empty we don't compare because it probably hasn't been typed into yet
        yield put(MODIFY_PASSWORDS_NOT_IDENTICAL(false));
    } else {
        yield put(MODIFY_PASSWORDS_NOT_IDENTICAL(password1 !== password2));
    }
}

export default function* userSaga() {
    yield takeLatest(MODIFY_PASSWORD1, comparePasswords);
    yield takeLatest(MODIFY_PASSWORD2, comparePasswords);
}
