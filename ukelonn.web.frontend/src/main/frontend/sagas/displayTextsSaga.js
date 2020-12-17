import { takeLatest, call, put, fork } from 'redux-saga/effects';
import axios from 'axios';
import {
    DISPLAY_TEXTS_REQUEST,
    DISPLAY_TEXTS_RECEIVE,
    DISPLAY_TEXTS_ERROR,
    DEFAULT_LOCALE_RECEIVE,
} from '../actiontypes';
import { stringify } from 'qs';

// watcher saga
export default function* displayTextsSaga() {
    yield takeLatest(DISPLAY_TEXTS_REQUEST, receiveDisplayTextsSaga);
    yield takeLatest(DEFAULT_LOCALE_RECEIVE, receiveDisplayTextsSaga);
}

function doDisplayTexts(locale) {
    return axios.get('/ukelonn/api/displaytexts?' + stringify({ locale }));
}

// worker saga
function* receiveDisplayTextsSaga(action) {
    try {
        const response = yield call(doDisplayTexts, action.payload);
        const displayTexts = (response.headers['content-type'] == 'application/json') ? response.data : {};
        yield put(DISPLAY_TEXTS_RECEIVE(displayTexts));
    } catch (error) {
        yield put(DISPLAY_TEXTS_ERROR(error));
    }
}
