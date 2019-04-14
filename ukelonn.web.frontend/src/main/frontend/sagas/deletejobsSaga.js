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
        const idsOfJobsToBeDeleted = action.jobsToDelete.map((job) => { return job.id; });
        const response = yield call(doDeleteJobs, { account: action.account, jobIds: idsOfJobsToBeDeleted });
        const jobs = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: DELETE_JOBS_RECEIVE, jobs });
    } catch (error) {
        yield put({ type: DELETE_JOBS_FAILURE, error });
    }
}
