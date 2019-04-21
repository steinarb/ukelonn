import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import {
    ACCOUNT_REQUEST,
    ACCOUNT_RECEIVE,
    ACCOUNT_FAILURE,
} from '../actiontypes';
import { emptyAccount } from './constants';


// watcher saga
export function* requestAccountSaga() {
    yield takeLatest(ACCOUNT_REQUEST, receiveAccountSaga);
}

function doAccount(username) {
    return axios.get('/ukelonn/api/account/' + username );
}

// worker saga
function* receiveAccountSaga(action) {
    try {
        const response = yield call(doAccount, action.payload);
        const account = (response.headers['content-type'] === 'application/json') ? response.data : emptyAccount;
        yield put(ACCOUNT_RECEIVE(account));
    } catch (error) {
        yield put(ACCOUNT_FAILURE(error));
    }
}
