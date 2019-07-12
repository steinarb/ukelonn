import { all, takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    EARNINGS_SUM_OVER_MONTH_REQUEST,
    EARNINGS_SUM_OVER_MONTH_RECEIVE,
    EARNINGS_SUM_OVER_MONTH_FAILURE,
    REGISTERJOB_RECEIVE,
} from '../actiontypes';

function doEarningsSumOverMonth(username) {
    return axios.get('/ukelonn/api/statistics/earnings/sumovermonth/' + username);
}

// worker saga
function* receiveEarningsSumOverMonthSaga(action) {
    try {
        const username = action.payload;
        const response = yield call(doEarningsSumOverMonth, username);
        const earningsSumOverMonth = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(EARNINGS_SUM_OVER_MONTH_RECEIVE(earningsSumOverMonth));
    } catch (error) {
        yield put(EARNINGS_SUM_OVER_MONTH_FAILURE(error));
    }
}

// watcher saga
export default function* earningsSumOverMonthSaga() {
    yield all([
        yield takeLatest(EARNINGS_SUM_OVER_MONTH_REQUEST, receiveEarningsSumOverMonthSaga),
    ]);
}
