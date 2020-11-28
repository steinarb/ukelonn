import { takeLatest, call, put, fork } from 'redux-saga/effects';
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

function doActivebonuses() {
    return axios.get('/ukelonn/api/activebonuses');
}

// worker saga
function* receiveActivebonusesSaga(action) {
    try {
        const response = yield call(doActivebonuses);
        const activebonuses = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(RECEIVE_ACTIVE_BONUSES(activebonuses));
    } catch (error) {
        yield put(RECEIVE_ACTIVE_BONUSES_FAILURE(error));
    }
}
