import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    CREATE_JOBTYPE_REQUEST,
    CREATE_JOBTYPE_RECEIVE,
    CREATE_JOBTYPE_FAILURE,
    CLEAR_JOB_TYPE_CREATE_FORM,
    CREATE_NEW_JOB_TYPE_BUTTON_CLICKED,
} from '../actiontypes';

function doCreateJobtype(jobtype) {
    return axios.post('/ukelonn/api/admin/jobtype/create', jobtype);
}

function* sendReceiveCreateJobtype(action) {
    try {
        const response = yield call(doCreateJobtype, action.payload);
        const jobtypes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(CREATE_JOBTYPE_RECEIVE(jobtypes));
        yield put(CLEAR_JOB_TYPE_CREATE_FORM());
    } catch (error) {
        yield put(CREATE_JOBTYPE_FAILURE(error));
    }
}

function* buildRequestAndSaveCreatedJobType() {
    const transactionTypeName = yield select(state => state.transactionTypeName);
    const transactionAmount = yield select(state => state.transactionAmount);
    yield put(CREATE_JOBTYPE_REQUEST({ transactionTypeName, transactionAmount }));
}

export default function* createJobtypeSaga() {
    yield takeLatest(CREATE_JOBTYPE_REQUEST, sendReceiveCreateJobtype);
    yield takeLatest(CREATE_NEW_JOB_TYPE_BUTTON_CLICKED, buildRequestAndSaveCreatedJobType);
}
