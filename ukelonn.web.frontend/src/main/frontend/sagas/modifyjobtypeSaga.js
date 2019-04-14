import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    MODIFY_JOBTYPE_REQUEST,
    MODIFY_JOBTYPE_RECEIVE,
    MODIFY_JOBTYPE_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestModifyJobtypeSaga() {
    yield takeLatest(MODIFY_JOBTYPE_REQUEST, receiveModifyJobtypeSaga);
}

function doModifyJobtype(jobtype) {
    return axios.post('/ukelonn/api/admin/jobtype/modify', jobtype);
}

// worker saga
function* receiveModifyJobtypeSaga(action) {
    try {
        const response = yield call(doModifyJobtype, action.transactiontype);
        const jobtypes = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: MODIFY_JOBTYPE_RECEIVE, jobtypes });
    } catch (error) {
        yield put({ type: MODIFY_JOBTYPE_FAILURE, error });
    }
}
