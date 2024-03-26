import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    EARNINGS_SUM_OVER_MONTH_REQUEST,
    EARNINGS_SUM_OVER_MONTH_RECEIVE,
    EARNINGS_SUM_OVER_MONTH_FAILURE,
} from '../actiontypes';

// watcher saga
export default function* earningsSumOverMonthSaga() {
    yield takeLatest(EARNINGS_SUM_OVER_MONTH_REQUEST, receiveEarningsSumOverMonthSaga);
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

function doEarningsSumOverMonth(username) {
    return axios.get('/api/statistics/earnings/sumovermonth/' + username);
}
