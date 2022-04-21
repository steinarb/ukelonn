import { takeLatest, put, select } from 'redux-saga/effects';
import {
    SELECT_JOB_TYPE,
    SELECTED_JOB_TYPE,
} from '../actiontypes';

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

export default function* jobSaga() {
    yield takeLatest(SELECT_JOB_TYPE, selectJobType);
}
