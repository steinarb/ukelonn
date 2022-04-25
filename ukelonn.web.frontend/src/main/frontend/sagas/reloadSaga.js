import { takeLatest } from 'redux-saga/effects';
import { RELOAD_WEB_PAGE } from '../actiontypes';

function reloadWebPage() {
    location.reload();
}

export default function* reloadSaga() {
    yield takeLatest(RELOAD_WEB_PAGE, reloadWebPage);
}
