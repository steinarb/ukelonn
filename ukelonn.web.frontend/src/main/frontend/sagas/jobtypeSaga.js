import { takeLatest, put, select } from 'redux-saga/effects';
import {
    SELECT_JOB_TYPE,
    SELECTED_JOB_TYPE,
    MODIFY_JOBTYPE_REQUEST,
    MODIFY_JOBTYPE_RECEIVE,
    SAVE_CHANGES_TO_JOB_TYPE_BUTTON_CLICKED,
    CLEAR_JOB_TYPE_FORM,
    CREATE_JOBTYPE_REQUEST,
    CREATE_JOBTYPE_RECEIVE,
    CREATE_NEW_JOB_TYPE_BUTTON_CLICKED,
    CLEAR_JOB_TYPE_CREATE_FORM,
} from '../actiontypes';

export default function* jobtypeSaga() {
    yield takeLatest(SELECT_JOB_TYPE, selectJobType);
    yield takeLatest(SAVE_CHANGES_TO_JOB_TYPE_BUTTON_CLICKED, buildRequestAndSaveModifiedJobType);
    yield takeLatest(CREATE_NEW_JOB_TYPE_BUTTON_CLICKED, buildRequestAndSaveCreatedJobType);
    yield takeLatest(MODIFY_JOBTYPE_RECEIVE, clearJobtypeForm);
    yield takeLatest(CREATE_JOBTYPE_RECEIVE, clearJobtypeCreateForm);
}

function* selectJobType(action) {
    const transactionTypeId = action.payload;
    if (transactionTypeId === -1) {
        yield put(SELECTED_JOB_TYPE({ transactionTypeId }));
    } else {
        const jobtypes = yield select(state => state.jobtypes);
        const jobtype = jobtypes.find(j => j.id === transactionTypeId);
        if (jobtype) {
            yield put(SELECTED_JOB_TYPE(jobtype));
        }
    }
}

function* buildRequestAndSaveModifiedJobType() {
    const jobType = yield select(state => ({
        id: state.transactionTypeId,
        transactionTypeName: state.transactionTypeName,
        transactionAmount: state.transactionAmount,
    }));
    yield put(MODIFY_JOBTYPE_REQUEST(jobType));
}

function* buildRequestAndSaveCreatedJobType() {
    const jobType = yield select(state => ({
        transactionTypeName: state.transactionTypeName,
        transactionAmount: state.transactionAmount,
    }));
    yield put(CREATE_JOBTYPE_REQUEST(jobType));
}

function* clearJobtypeForm() {
    yield put(CLEAR_JOB_TYPE_FORM());
}

function* clearJobtypeCreateForm() {
    yield put(CLEAR_JOB_TYPE_CREATE_FORM());
}
