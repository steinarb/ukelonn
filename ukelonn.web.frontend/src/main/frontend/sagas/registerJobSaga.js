import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    REGISTERJOB_REQUEST,
    REGISTERJOB_RECEIVE,
    REGISTERJOB_FAILURE,
    EARNINGS_SUM_OVER_YEAR_REQUEST,
    EARNINGS_SUM_OVER_MONTH_REQUEST,
    REGISTER_JOB_BUTTON_CLICKED,
    CLEAR_REGISTER_JOB_FORM,
} from '../actiontypes';
import { emptyAccount } from '../constants';

function doRegisterJob(performedJob) {
    return axios.post('/ukelonn/api/job/register', performedJob);
}

function* requestReceiveRegisterJobSaga(action) {
    try {
        const response = yield call(doRegisterJob, action.payload);
        const account = (response.headers['content-type'] == 'application/json') ? response.data : emptyAccount;
        yield put(REGISTERJOB_RECEIVE(account));
    } catch (error) {
        yield put(REGISTERJOB_FAILURE(error));
    }
}

function* registerPerformedJob() {
    const accountId = yield select(state => state.accountId);
    const username = yield select(state => state.accountUsername);
    const account = { accountId, username };
    const transactionTypeId = yield select(state => state.transactionTypeId);
    const transactionAmount = yield select(state => state.transactionAmount);
    const transactionDate = yield select(state => state.transactionDate);
    yield put(REGISTERJOB_REQUEST({ account, transactionTypeId, transactionAmount, transactionDate }));
}

function* fetchUpdatedSumsAndClearRegisterJobForm(action) {
    const username = action.payload.username;
    yield put(EARNINGS_SUM_OVER_YEAR_REQUEST(username));
    yield put(EARNINGS_SUM_OVER_MONTH_REQUEST(username));
    yield put(CLEAR_REGISTER_JOB_FORM());
}

export default function* registerJobSaga() {
    yield takeLatest(REGISTERJOB_REQUEST, requestReceiveRegisterJobSaga);
    yield takeLatest(REGISTER_JOB_BUTTON_CLICKED, registerPerformedJob);
    yield takeLatest(REGISTERJOB_RECEIVE, fetchUpdatedSumsAndClearRegisterJobForm);
}
