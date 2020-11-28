import { takeLatest, call, put, fork } from 'redux-saga/effects';
import axios from 'axios';
import {
    GET_ALL_BONUSES,
    RECEIVE_ALL_BONUSES,
    RECEIVE_ALL_BONUSES_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestAllbonusesSaga() {
    yield takeLatest(GET_ALL_BONUSES, receiveAllbonusesSaga);
}

function doAllbonuses() {
    return axios.get('/ukelonn/api/allbonuses');
}

// worker saga
function* receiveAllbonusesSaga(action) {
    try {
        const response = yield call(doAllbonuses);
        const allbonuses = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(RECEIVE_ALL_BONUSES(allbonuses));
    } catch (error) {
        yield put(RECEIVE_ALL_BONUSES_FAILURE(error));
    }
}
