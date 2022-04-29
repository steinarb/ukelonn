import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    MODIFY_JOBTYPE_REQUEST,
    MODIFY_JOBTYPE_RECEIVE,
    MODIFY_JOBTYPE_FAILURE,
} from '../actiontypes';

function doModifyJobtype(jobtype) {
    return axios.post('/ukelonn/api/admin/jobtype/modify', jobtype);
}

function* receiveModifyJobtypeSaga(action) {
    try {
        const response = yield call(doModifyJobtype, action.payload);
        const jobtypes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(MODIFY_JOBTYPE_RECEIVE(jobtypes));
    } catch (error) {
        yield put(MODIFY_JOBTYPE_FAILURE(error));
    }
}

export default function* modifyJobtypeSaga() {
    yield takeLatest(MODIFY_JOBTYPE_REQUEST, receiveModifyJobtypeSaga);
}
