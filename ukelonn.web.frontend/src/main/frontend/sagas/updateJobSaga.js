import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    UPDATE_JOB_REQUEST,
    UPDATE_JOB_RECEIVE,
    UPDATE_JOB_FAILURE,
    CLEAR_EDIT_JOB_FORM,
    SAVE_CHANGES_TO_JOB_BUTTON_CLICKED,
} from '../actiontypes';

function doUpdateJob(updatedJob) {
    return axios.post('/ukelonn/api/job/update', updatedJob);
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

function* clearEditJobForm() {
    yield put(CLEAR_EDIT_JOB_FORM());
}

function* buildRequestAndSaveModifiedJob() {
    const id = yield select(state => state.transactionId);
    const accountId = yield select(state => state.accountId);
    const transactionTypeId = yield select(state => state.transactionTypeId);
    const transactionAmount = yield select(state => state.transactionAmount);
    const transactionTime = yield select(state => state.transactionDate);
    yield put(UPDATE_JOB_REQUEST({ id, accountId, transactionTypeId, transactionAmount, transactionTime }));
}

export default function* updateJobSaga() {
    yield takeLatest(UPDATE_JOB_REQUEST, requestReceiveUpdateJobSaga);
    yield takeLatest(UPDATE_JOB_RECEIVE, clearEditJobForm);
    yield takeLatest(SAVE_CHANGES_TO_JOB_BUTTON_CLICKED, buildRequestAndSaveModifiedJob);
}
