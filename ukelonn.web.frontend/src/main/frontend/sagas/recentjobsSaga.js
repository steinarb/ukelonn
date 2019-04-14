import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    RECENTJOBS_REQUEST,
    RECENTJOBS_RECEIVE,
    RECENTJOBS_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestRecentJobsSaga() {
    yield takeLatest(RECENTJOBS_REQUEST, receiveRecentJobsSaga);
}

function doRecentJobs(accountId) {
    return axios.get('/ukelonn/api/jobs/' + accountId);
}

// worker saga
function* receiveRecentJobsSaga(action) {
    try {
        const response = yield call(doRecentJobs, action.accountId);
        const jobs = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: RECENTJOBS_RECEIVE, jobs: jobs });
    } catch (error) {
        yield put({ type: RECENTJOBS_FAILURE, error });
    }
}
