import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    JOBTYPELIST_REQUEST,
    JOBTYPELIST_RECEIVE,
    JOBTYPELIST_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestJobtypeListSaga() {
    yield takeLatest(JOBTYPELIST_REQUEST, receiveJobtypeListSaga);
}

function doJobtypeList() {
    return axios.get('/ukelonn/api/jobtypes');
}

// worker saga
function* receiveJobtypeListSaga(action) {
    try {
        const response = yield call(doJobtypeList);
        const jobtypes = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(JOBTYPELIST_RECEIVE(jobtypes));
    } catch (error) {
        yield put(JOBTYPELIST_FAILURE(error));
    }
}
