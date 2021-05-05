import { takeLatest, put } from 'redux-saga/effects';
import Cookies from 'js-cookie';
import {
    UPDATE_LOCALE,
    DEFAULT_LOCALE_RECEIVE,
} from '../actiontypes';

export default function* localeSaga() {
    yield takeLatest(DEFAULT_LOCALE_RECEIVE, setLocaleCookieIfNotPresentAndPutCookieValueAsLocale);
    yield takeLatest(UPDATE_LOCALE, updateLocaleCookie);
}

export function* setLocaleCookieIfNotPresentAndPutCookieValueAsLocale(action) {
    const currentLocale = Cookies.get('locale');
    if (!currentLocale) {
        Cookies.set('locale', action.payload);
    }
    yield put(UPDATE_LOCALE(Cookies.get('locale')));

}

function* updateLocaleCookie(action) {
    yield Cookies.set('locale', action.payload);
}
