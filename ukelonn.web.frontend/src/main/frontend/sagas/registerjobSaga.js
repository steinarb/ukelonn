import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    REGISTERJOB_REQUEST,
    REGISTERJOB_RECEIVE,
    REGISTERJOB_FAILURE,
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
        const response = yield call(doRegisterJob, action.performedjob);
        const account = (response.headers['content-type'] == 'application/json') ? response.data : emptyAccount;
        yield put({ type: REGISTERJOB_RECEIVE, account: account });
    } catch (error) {
        yield put({ type: REGISTERJOB_FAILURE, error });
    }
}
