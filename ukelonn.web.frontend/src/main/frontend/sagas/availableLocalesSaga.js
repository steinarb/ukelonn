import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    AVAILABLE_LOCALES_REQUEST,
    AVAILABLE_LOCALES_RECEIVE,
    AVAILABLE_LOCALES_ERROR,
} from '../actiontypes';

// watcher saga
export default function* availableLocalesSaga() {
    yield takeLatest(AVAILABLE_LOCALES_REQUEST, receiveAvailableLocalesSaga);
}

function doAvailableLocales() {
    return axios.get('/api/availablelocales');
}

// worker saga
function* receiveAvailableLocalesSaga() {
    try {
        const response = yield call(doAvailableLocales);
        const availableLocales = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(AVAILABLE_LOCALES_RECEIVE(availableLocales));
    } catch (error) {
        yield put(AVAILABLE_LOCALES_ERROR(error));
    }
}
