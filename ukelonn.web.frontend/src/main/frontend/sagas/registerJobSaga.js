import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    REGISTERJOB_REQUEST,
    REGISTERJOB_RECEIVE,
    REGISTERJOB_FAILURE,
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

export default function* registerJobSaga() {
    yield takeLatest(REGISTERJOB_REQUEST, requestReceiveRegisterJobSaga);
}
