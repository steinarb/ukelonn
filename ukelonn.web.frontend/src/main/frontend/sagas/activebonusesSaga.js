import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    GET_ACTIVE_BONUSES,
    RECEIVE_ACTIVE_BONUSES,
    RECEIVE_ACTIVE_BONUSES_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestActivebonusesSaga() {
    yield takeLatest(GET_ACTIVE_BONUSES, receiveActivebonusesSaga);
}

// worker saga
function* receiveActivebonusesSaga() {
    try {
        const response = yield call(doActivebonuses);
        const activebonuses = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(RECEIVE_ACTIVE_BONUSES(activebonuses));
    } catch (error) {
        yield put(RECEIVE_ACTIVE_BONUSES_FAILURE(error));
    }
}

function doActivebonuses() {
    return axios.get('/api/activebonuses');
}
