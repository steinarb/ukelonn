import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    REGISTERJOB_REQUEST,
    REGISTERJOB_RECEIVE,
    REGISTERJOB_FAILURE,
    EARNINGS_SUM_OVER_YEAR_REQUEST,
    EARNINGS_SUM_OVER_MONTH_REQUEST,
} from '../actiontypes';
import { emptyAccount } from './constants';


// watcher saga
export function* requestRegisterJobSaga() {
    yield takeLatest(REGISTERJOB_REQUEST, receiveRegisterJobSaga);
}

function doRegisterJob(performedJob) {
    return axios.post('/ukelonn/api/job/register', performedJob);
}

// worker saga
function* receiveRegisterJobSaga(action) {
    try {
        const response = yield call(doRegisterJob, action.payload);
        const account = (response.headers['content-type'] == 'application/json') ? response.data : emptyAccount;
        yield put(REGISTERJOB_RECEIVE(account));
        const username = account.username;
        yield put(EARNINGS_SUM_OVER_YEAR_REQUEST(username));
        yield put(EARNINGS_SUM_OVER_MONTH_REQUEST(username));
    } catch (error) {
        yield put(REGISTERJOB_FAILURE(error));
    }
}
