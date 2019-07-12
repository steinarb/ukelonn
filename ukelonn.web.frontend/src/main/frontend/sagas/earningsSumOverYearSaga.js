import { all, takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    EARNINGS_SUM_OVER_YEAR_REQUEST,
    EARNINGS_SUM_OVER_YEAR_RECEIVE,
    EARNINGS_SUM_OVER_YEAR_FAILURE,
    REGISTERJOB_RECEIVE,
} from '../actiontypes';

function doEarningsSumOverYear(username) {
    return axios.get('/ukelonn/api/statistics/earnings/sumoveryear/' + username);
}

// worker saga
function* receiveEarningsSumOverYearSaga(action) {
    try {
        const username = action.payload;
        const response = yield call(doEarningsSumOverYear, username);
        const earningsSumOverYear = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(EARNINGS_SUM_OVER_YEAR_RECEIVE(earningsSumOverYear));
    } catch (error) {
        yield put(EARNINGS_SUM_OVER_YEAR_FAILURE(error));
    }
}

// watcher saga
export default function* earningsSumOverYearSaga() {
    yield all([
        takeLatest(EARNINGS_SUM_OVER_YEAR_REQUEST, receiveEarningsSumOverYearSaga),
    ]);
}
