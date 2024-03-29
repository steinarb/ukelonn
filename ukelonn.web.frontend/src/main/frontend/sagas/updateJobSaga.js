import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    UPDATE_JOB_REQUEST,
    UPDATE_JOB_RECEIVE,
    UPDATE_JOB_FAILURE,
} from '../actiontypes';

export default function* updateJobSaga() {
    yield takeLatest(UPDATE_JOB_REQUEST, requestReceiveUpdateJobSaga);
}

function* requestReceiveUpdateJobSaga(action) {
    try {
        const response = yield call(doUpdateJob, action.payload);
        const jobs = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(UPDATE_JOB_RECEIVE(jobs));
    } catch (error) {
        yield put(UPDATE_JOB_FAILURE(error));
    }
}

function doUpdateJob(updatedJob) {
    return axios.post('/api/job/update', updatedJob);
}
