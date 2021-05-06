import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    UPDATE_ACCOUNT,
    ACCOUNT_REQUEST,
    ACCOUNT_RECEIVE,
    ACCOUNT_FAILURE,
    EARNINGS_SUM_OVER_YEAR_REQUEST,
    EARNINGS_SUM_OVER_YEAR_RECEIVE,
    EARNINGS_SUM_OVER_MONTH_REQUEST,
    EARNINGS_SUM_OVER_MONTH_RECEIVE,
    RECEIVED_NOTIFICATION,
} from '../actiontypes';
import { emptyAccount } from '../constants';
import { findUsername } from '../common/login';


function doAccount(username) {
    return axios.get('/ukelonn/api/account/' + username );
}

// worker saga
function* updateAccountSaga(action) {
    const payload = action.payload || {};
    const { username } = payload;
    yield put(EARNINGS_SUM_OVER_YEAR_REQUEST(username));
    yield put(EARNINGS_SUM_OVER_MONTH_REQUEST(username));
}

// worker saga
function* receiveAccountSaga(action) {
    if (action.payload) {
        try {
            const response = yield call(doAccount, action.payload);
            const account = (response.headers['content-type'] === 'application/json') ? response.data : emptyAccount;
            const username = account.username;
            yield put(ACCOUNT_RECEIVE(account));
            yield put(EARNINGS_SUM_OVER_YEAR_REQUEST(username));
            yield put(EARNINGS_SUM_OVER_MONTH_REQUEST(username));
        } catch (error) {
            yield put(ACCOUNT_FAILURE(error));
        }
    } else {
        yield put(ACCOUNT_RECEIVE(emptyAccount));
        yield put(EARNINGS_SUM_OVER_YEAR_RECEIVE([]));
        yield put(EARNINGS_SUM_OVER_MONTH_RECEIVE([]));
    }
}

function* updateAccountOnNotification() {
    const username = yield select(findUsername);
    yield put(ACCOUNT_REQUEST(username));
}

// watcher saga
export default function* accountSaga() {
    yield takeLatest(UPDATE_ACCOUNT, updateAccountSaga);
    yield takeLatest(ACCOUNT_REQUEST, receiveAccountSaga);
    yield takeLatest(RECEIVED_NOTIFICATION, updateAccountOnNotification);
}
