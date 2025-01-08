import { createReducer } from '@reduxjs/toolkit';
import Cookies from 'js-cookie';
import { UPDATE_LOCALE } from '../actiontypes';
import { api } from '../api';

const currentLocale = Cookies.get('locale') || '';

const localeReducer = createReducer(currentLocale, builder => {
    builder
        .addCase(UPDATE_LOCALE, (state, action) => updateLocaleCookie(action))
        .addMatcher(api.endpoints.getDefaultlocale.matchFulfilled, (_, action) => setLocaleCookieIfNotPresentAndPutCookieValueAsLocale(action));
});

export default localeReducer;

function setLocaleCookieIfNotPresentAndPutCookieValueAsLocale(action) {
    const currentLocale = Cookies.get('locale');
    if (!currentLocale) {
        Cookies.set('locale', action.payload);
    }

    return Cookies.get('locale');
}

function updateLocaleCookie(action) {
    Cookies.set('locale', action.payload);
    return action.payload;
}
