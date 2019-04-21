import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    DELETE_JOBS_REQUEST,
    DELETE_JOBS_RECEIVE,
    DELETE_JOBS_FAILURE,
} from '../actiontypes';

// Watcher saga
export function* requestDeleteJobsSaga() {
    yield takeLatest(DELETE_JOBS_REQUEST, receiveDeleteJobsSaga);
}

function doDeleteJobs(accountWithJobIds) {
    return axios.post('/ukelonn/api/admin/jobs/delete', accountWithJobIds);
}

// worker saga
function* receiveDeleteJobsSaga(action) {
    try {
        const payload = action.payload || {};
        const jobsToDelete = payload.jobsToDelete || [];
        const idsOfJobsToBeDeleted = jobsToDelete.map((job) => { return job.id; });
        const response = yield call(doDeleteJobs, { account: payload.account, jobIds: idsOfJobsToBeDeleted });
        const jobs = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(DELETE_JOBS_RECEIVE(jobs));
    } catch (error) {
        yield put(DELETE_JOBS_FAILURE(error));
    }
}
