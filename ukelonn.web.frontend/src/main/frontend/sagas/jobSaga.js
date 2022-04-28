import { takeLatest, put, select } from 'redux-saga/effects';
import {
    EARNINGS_SUM_OVER_YEAR_REQUEST,
    EARNINGS_SUM_OVER_MONTH_REQUEST,
    REGISTERJOB_REQUEST,
    REGISTERJOB_RECEIVE,
    REGISTER_JOB_BUTTON_CLICKED,
    UPDATE_JOB_REQUEST,
    UPDATE_JOB_RECEIVE,
    SAVE_CHANGES_TO_JOB_BUTTON_CLICKED,
    DELETE_JOBS_REQUEST,
    DELETE_SELECTED_JOBS_BUTTON_CLICKED,
    CLEAR_REGISTER_JOB_FORM,
    CLEAR_EDIT_JOB_FORM,
} from '../actiontypes';

function* registerPerformedJob() {
    const accountId = yield select(state => state.accountId);
    const username = yield select(state => state.accountUsername);
    const account = { accountId, username };
    const transactionTypeId = yield select(state => state.transactionTypeId);
    const transactionAmount = yield select(state => state.transactionAmount);
    const transactionDate = yield select(state => state.transactionDate);
    yield put(REGISTERJOB_REQUEST({ account, transactionTypeId, transactionAmount, transactionDate }));
}

function* buildRequestAndSaveModifiedJob() {
    const id = yield select(state => state.transactionId);
    const accountId = yield select(state => state.accountId);
    const transactionTypeId = yield select(state => state.transactionTypeId);
    const transactionAmount = yield select(state => state.transactionAmount);
    const transactionTime = yield select(state => state.transactionDate);
    yield put(UPDATE_JOB_REQUEST({ id, accountId, transactionTypeId, transactionAmount, transactionTime }));
}

function* deleteSelectedJobs() {
    const accountId = yield select(state => state.accountId);
    const jobs = yield select(state => state.jobs);
    const jobsToDelete = jobs.filter(job => job.delete);
    yield put(DELETE_JOBS_REQUEST({ account: { accountId }, jobsToDelete }));
}

function* fetchUpdatedSumsAndClearRegisterJobForm(action) {
    const username = action.payload.username;
    yield put(EARNINGS_SUM_OVER_YEAR_REQUEST(username));
    yield put(EARNINGS_SUM_OVER_MONTH_REQUEST(username));
    yield put(CLEAR_REGISTER_JOB_FORM());
}

function* clearEditJobForm() {
    yield put(CLEAR_EDIT_JOB_FORM());
}

export default function* jobSaga() {
    yield takeLatest(REGISTER_JOB_BUTTON_CLICKED, registerPerformedJob);
    yield takeLatest(SAVE_CHANGES_TO_JOB_BUTTON_CLICKED, buildRequestAndSaveModifiedJob);
    yield takeLatest(DELETE_SELECTED_JOBS_BUTTON_CLICKED, deleteSelectedJobs);
    yield takeLatest(REGISTERJOB_RECEIVE, fetchUpdatedSumsAndClearRegisterJobForm);
    yield takeLatest(UPDATE_JOB_RECEIVE, clearEditJobForm);
}
