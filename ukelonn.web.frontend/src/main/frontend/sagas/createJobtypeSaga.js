import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    CREATE_JOBTYPE_REQUEST,
    CREATE_JOBTYPE_RECEIVE,
    CREATE_JOBTYPE_FAILURE,
} from '../actiontypes';

export default function* createJobtypeSaga() {
    yield takeLatest(CREATE_JOBTYPE_REQUEST, sendReceiveCreateJobtype);
}

function* sendReceiveCreateJobtype(action) {
    try {
        const response = yield call(doCreateJobtype, action.payload);
        const jobtypes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(CREATE_JOBTYPE_RECEIVE(jobtypes));
    } catch (error) {
        yield put(CREATE_JOBTYPE_FAILURE(error));
    }
}

function doCreateJobtype(jobtype) {
    return axios.post('/api/admin/jobtype/create', jobtype);
}
