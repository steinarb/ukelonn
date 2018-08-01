import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";

// watcher saga
export function* requestInitialLoginStateSaga() {
    yield takeLatest("INITIAL_LOGIN_STATE_REQUEST", receiveInitialLoginStateSaga);
}

function doGetLogin() {
    return axios.get('/ukelonn/api/login');
}

// worker saga
export function* receiveInitialLoginStateSaga() {
    try {
        const response = yield call(doGetLogin);
        const loginResponse = response.data;
        yield put({ type: 'INITIAL_LOGIN_STATE_RECEIVE', loginResponse: loginResponse });
    } catch (error) {
        yield put({ type: 'INITIAL_LOGIN_STATE_FAILURE', error });
    }
}

// watcher saga
export function* requestLoginSaga() {
    yield takeLatest("LOGIN_REQUEST", receiveLoginSaga);
}

function doLogin(username, password) {
    return axios.post('/ukelonn/api/login', { username, password });
}

// worker saga
function* receiveLoginSaga(action) {
    try {
        const response = yield call(doLogin, action.username, action.password);
        const loginResponse = response.data;
        yield put({ type: 'LOGIN_RECEIVE', loginResponse: loginResponse });
    } catch (error) {
        yield put({ type: 'LOGIN_FAILURE', error });
    }
}

// watcher saga
export function* requestLogoutSaga() {
    yield takeLatest("LOGOUT_REQUEST", receiveLogoutSaga);
}

function doLogout() {
    return axios.post('/ukelonn/api/logout', {});
}

// worker saga
function* receiveLogoutSaga(action) {
    try {
        const response = yield call(doLogout);
        const loginResponse = response.data;
        yield put({ type: 'LOGOUT_RECEIVE', loginResponse: loginResponse });
    } catch (error) {
        yield put({ type: 'LOGOUT_FAILURE', error });
    }
}


// watcher saga
export function* requestAccountSaga() {
    yield takeLatest("ACCOUNT_REQUEST", receiveAccountSaga);
}

function doAccount(username) {
    return axios.get('/ukelonn/api/account/' + username );
}

// worker saga
function* receiveAccountSaga(action) {
    try {
        const response = yield call(doAccount, action.username);
        const account = response.data;
        yield put({ type: 'ACCOUNT_RECEIVE', account: account });
    } catch (error) {
        yield put({ type: 'ACCOUNT_FAILURE', error });
    }
}


// watcher saga
export function* requestJobtypeListSaga() {
    yield takeLatest("JOBTYPELIST_REQUEST", receiveJobtypeListSaga);
}

function doJobtypeList() {
    return axios.get('/ukelonn/api/jobtypes');
}

// worker saga
function* receiveJobtypeListSaga(action) {
    try {
        const response = yield call(doJobtypeList);
        const jobtypes = response.data;
        yield put({ type: 'JOBTYPELIST_RECEIVE', jobtypes: jobtypes });
    } catch (error) {
        yield put({ type: 'JOBTYPELIST_FAILURE', error });
    }
}


// watcher saga
export function* requestRegisterJobSaga() {
    yield takeLatest("REGISTERJOB_REQUEST", receiveRegisterJobSaga);
}

function doRegisterJob(performedJob) {
    return axios.post('/ukelonn/api/registerjob', performedJob);
}

// worker saga
function* receiveRegisterJobSaga(action) {
    try {
        const response = yield call(doRegisterJob, action.performedjob);
        const account = response.data;
        yield put({ type: 'REGISTERJOB_RECEIVE', account: account });
    } catch (error) {
        yield put({ type: 'REGISTERJOB_FAILURE', error });
    }
}


// watcher saga
export function* requestRecentJobsSaga() {
    yield takeLatest("RECENTJOBS_REQUEST", receiveRecentJobsSaga);
}

function doRecentJobs(account) {
    return axios.get('/ukelonn/api/jobs/' + account.accountId);
}

// worker saga
function* receiveRecentJobsSaga(action) {
    try {
        const response = yield call(doRecentJobs, action.account);
        const jobs = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: 'RECENTJOBS_RECEIVE', jobs: jobs });
    } catch (error) {
        yield put({ type: 'RECENTJOBS_FAILURE', error });
    }
}


// watcher saga
export function* requestRecentPaymentsSaga() {
    yield takeLatest("RECENTPAYMENTS_REQUEST", receiveRecentPaymentsSaga);
}

function doRecentPayments(account) {
    return axios.get('/ukelonn/api/payments/' + account.accountId);
}

// worker saga
function* receiveRecentPaymentsSaga(action) {
    try {
        const response = yield call(doRecentPayments, action.account);
        const payments = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: 'RECENTPAYMENTS_RECEIVE', payments: payments });
    } catch (error) {
        yield put({ type: 'RECENTPAYMENTS_FAILURE', error });
    }
}


// watcher saga
export function* requestAccountsSaga() {
    yield takeLatest("ACCOUNTS_REQUEST", receiveAccountsSaga);
}

function doAccounts() {
    return axios.get('/ukelonn/api/accounts');
}

// worker saga
function* receiveAccountsSaga(action) {
    try {
        const response = yield call(doAccounts);
        const accounts = response.data;
        yield put({ type: 'ACCOUNTS_RECEIVE', accounts: accounts });
    } catch (error) {
        yield put({ type: 'ACCOUNTS_FAILURE', error });
    }
}


// watcher saga
export function* requestPaymenttypesSaga() {
    yield takeLatest("PAYMENTTYPES_REQUEST", receivePaymenttypesSaga);
}

function doPaymenttypes() {
    return axios.get('/ukelonn/api/paymenttypes');
}

// worker saga
function* receivePaymenttypesSaga(action) {
    try {
        const response = yield call(doPaymenttypes);
        const paymenttypes = response.data;
        yield put({ type: 'PAYMENTTYPES_RECEIVE', paymenttype: paymenttypes[0], paymenttypes: paymenttypes });
    } catch (error) {
        yield put({ type: 'PAYMENTTYPES_FAILURE', error });
    }
}


// watcher saga
export function* requestRegisterPaymentSaga() {
    yield takeLatest("REGISTERPAYMENT_REQUEST", receiveRegisterPaymentSaga);
}

function doRegisterPayment(payment) {
    return axios.post('/ukelonn/api/registerpayment', payment);
}

// worker saga
function* receiveRegisterPaymentSaga(action) {
    try {
        const response = yield call(doRegisterPayment, action.payment);
        const account = response.data;
        yield put({ type: 'REGISTERPAYMENT_RECEIVE', account: account });
    } catch (error) {
        yield put({ type: 'REGISTERPAYMENT_FAILURE', error });
    }
}


export function* rootSaga() {
    yield [
        fork(requestInitialLoginStateSaga),
        fork(requestLoginSaga),
        fork(requestLogoutSaga),
        fork(requestAccountSaga),
        fork(requestJobtypeListSaga),
        fork(requestRegisterJobSaga),
        fork(requestRecentJobsSaga),
        fork(requestRecentPaymentsSaga),
        fork(requestAccountsSaga),
        fork(requestPaymenttypesSaga),
        fork(requestRegisterPaymentSaga),
    ];
};
