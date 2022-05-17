import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    SELECT_ACCOUNT,
    ACCOUNT_REQUEST,
    ACCOUNT_RECEIVE,
    ACCOUNT_FAILURE,
    EARNINGS_SUM_OVER_YEAR_REQUEST,
    EARNINGS_SUM_OVER_MONTH_REQUEST,
    RECEIVED_NOTIFICATION,
    RECENTPAYMENTS_REQUEST,
    RECENTJOBS_REQUEST,
} from '../actiontypes';
import { emptyAccount } from '../constants';
import { findUsername } from '../common/login';


function doAccount(username) {
    return axios.get('/ukelonn/api/account/' + username );
}

function* requestReceiveAccountSaga(action) {
    if (action.payload) {
        try {
            const response = yield call(doAccount, action.payload);
            const account = (response.headers['content-type'] === 'application/json') ? response.data : emptyAccount;
            yield put(ACCOUNT_RECEIVE(account));
            const username = account.username;
            yield put(EARNINGS_SUM_OVER_YEAR_REQUEST(username));
            yield put(EARNINGS_SUM_OVER_MONTH_REQUEST(username));
        } catch (error) {
            yield put(ACCOUNT_FAILURE(error));
        }
    }
}

function* fetchDataForSelectedAccount(action) {
    const { accountId, username } = action.payload;
    if (accountId !== -1) {
        if (username) {
            yield put(EARNINGS_SUM_OVER_YEAR_REQUEST(username));
            yield put(EARNINGS_SUM_OVER_MONTH_REQUEST(username));
        }
        if (accountId) {
            yield put(RECENTPAYMENTS_REQUEST(accountId));
            yield put(RECENTJOBS_REQUEST(accountId));
        }
    }
}

function* updateAccountOnNotification() {
    const username = yield select(findUsername);
    yield put(ACCOUNT_REQUEST(username));
}

export default function* accountSaga() {
    yield takeLatest(SELECT_ACCOUNT, fetchDataForSelectedAccount);
    yield takeLatest(ACCOUNT_REQUEST, requestReceiveAccountSaga);
    yield takeLatest(RECEIVED_NOTIFICATION, updateAccountOnNotification);
}
