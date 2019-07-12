import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    ACCOUNT_REQUEST,
    ACCOUNT_RECEIVE,
    ACCOUNT_FAILURE,
    EARNINGS_SUM_OVER_YEAR_REQUEST,
    EARNINGS_SUM_OVER_MONTH_REQUEST,
} from '../actiontypes';
import { emptyAccount } from './constants';


function doAccount(username) {
    return axios.get('/ukelonn/api/account/' + username );
}

// worker saga
function* receiveAccountSaga(action) {
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
}

// watcher saga
export default function* accountSaga() {
    yield takeLatest(ACCOUNT_REQUEST, receiveAccountSaga);
}
