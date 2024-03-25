import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    DELETE_JOBS_REQUEST,
    DELETE_JOBS_RECEIVE,
    DELETE_JOBS_FAILURE,
} from '../actiontypes';

export default function* deleteJobsSaga() {
    yield takeLatest(DELETE_JOBS_REQUEST, requestReceiveDeleteJobsSaga);
}

function* requestReceiveDeleteJobsSaga(action) {
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

function doDeleteJobs(accountWithJobIds) {
    return axios.post('/api/admin/jobs/delete', accountWithJobIds);
}
