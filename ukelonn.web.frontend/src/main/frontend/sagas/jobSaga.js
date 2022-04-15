import { takeLatest, put, select } from 'redux-saga/effects';
import {
    SELECT_JOB_TYPE,
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_JOB_AMOUNT,
    CLEAR_JOB_FORM,
} from '../actiontypes';

function* selectJobType(action) {
    const transactionTypeId = action.payload;
    if (transactionTypeId === -1) {
        yield put(CLEAR_JOB_FORM());
    }
    const jobtypes = yield select(state => state.jobtypes);
    const jobtype = jobtypes.find(j => j.id === transactionTypeId);
    if (jobtype) {
        yield put(MODIFY_TRANSACTION_TYPE_NAME(jobtype.transactionTypeName));
        yield put(MODIFY_JOB_AMOUNT(jobtype.transactionAmount));
    }
}

export default function* jobSaga() {
    yield takeLatest(SELECT_JOB_TYPE, selectJobType);
}
