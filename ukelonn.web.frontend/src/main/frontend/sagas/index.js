import { takeLatest, call, put, fork } from "redux-saga/effects";
import axios from "axios";
import delay from "delay";

// Constants
const emptyLoginResponse = { username: '', roles: [], error: '' };
const emptyAccount = { firstName: 'Ukjent', fullName: '', balance: 0.0 };


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
        const loginResponse = (response.headers['content-type'] == 'application/json') ? response.data : emptyLoginResponse;
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
        const loginResponse = (response.headers['content-type'] == 'application/json') ? response.data : emptyLoginResponse;
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
        const loginResponse = (response.headers['content-type'] == 'application/json') ? response.data : emptyLoginResponse;
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
        const account = (response.headers['content-type'] == 'application/json') ? response.data : emptyAccount;
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
        const jobtypes = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: 'JOBTYPELIST_RECEIVE', jobtypes: jobtypes });
    } catch (error) {
        yield put({ type: 'JOBTYPELIST_FAILURE', error });
    }
}


// watcher saga
export function* requestPaymenttypeListSaga() {
    yield takeLatest("PAYMENTTYPELIST_REQUEST", receivePaymenttypeListSaga);
}

function doPaymenttypeList() {
    return axios.get('/ukelonn/api/paymenttypes');
}

// worker saga
function* receivePaymenttypeListSaga(action) {
    try {
        const response = yield call(doPaymenttypeList);
        const paymenttypes = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: 'PAYMENTTYPELIST_RECEIVE', paymenttypes: paymenttypes });
    } catch (error) {
        yield put({ type: 'PAYMENTTYPELIST_FAILURE', error });
    }
}


// watcher saga
export function* requestRegisterJobSaga() {
    yield takeLatest("REGISTERJOB_REQUEST", receiveRegisterJobSaga);
}

function doRegisterJob(performedJob) {
    return axios.post('/ukelonn/api/job/register', performedJob);
}

// worker saga
function* receiveRegisterJobSaga(action) {
    try {
        const response = yield call(doRegisterJob, action.performedjob);
        const account = (response.headers['content-type'] == 'application/json') ? response.data : emptyAccount;
        yield put({ type: 'REGISTERJOB_RECEIVE', account: account });
    } catch (error) {
        yield put({ type: 'REGISTERJOB_FAILURE', error });
    }
}


// watcher saga
export function* requestRecentJobsSaga() {
    yield takeLatest("RECENTJOBS_REQUEST", receiveRecentJobsSaga);
}

function doRecentJobs(accountId) {
    return axios.get('/ukelonn/api/jobs/' + accountId);
}

// worker saga
function* receiveRecentJobsSaga(action) {
    try {
        const response = yield call(doRecentJobs, action.accountId);
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

function doRecentPayments(accountId) {
    return axios.get('/ukelonn/api/payments/' + accountId);
}

// worker saga
function* receiveRecentPaymentsSaga(action) {
    try {
        const response = yield call(doRecentPayments, action.accountId);
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
        const accounts = (response.headers['content-type'] == 'application/json') ? response.data : [];
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
        const paymenttypes = (response.headers['content-type'] == 'application/json') ? response.data : [];
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

function doNotifyPaymentdone(payment, paymenttype) {
    const notification = {
        title: 'Ukelønn',
        message: payment.transactionAmount + ' kroner ' + paymenttype.transactionTypeName,
    };
    return axios.post('/ukelonn/api/notificationto/' + payment.account.username, notification);
}

// worker saga
function* receiveRegisterPaymentSaga(action) {
    try {
        const response = yield call(doRegisterPayment, action.payment);
        const account = (response.headers['content-type'] == 'application/json') ? response.data : emptyAccount;
        doNotifyPaymentdone(action.payment, action.paymenttype);
        yield put({ type: 'REGISTERPAYMENT_RECEIVE', account: account });
    } catch (error) {
        yield put({ type: 'REGISTERPAYMENT_FAILURE', error });
    }
}


// watcher saga
export function* requestModifyJobtypeSaga() {
    yield takeLatest("MODIFY_JOBTYPE_REQUEST", receiveModifyJobtypeSaga);
}

function doModifyJobtype(jobtype) {
    return axios.post('/ukelonn/api/admin/jobtype/modify', jobtype);
}

// worker saga
function* receiveModifyJobtypeSaga(action) {
    try {
        const response = yield call(doModifyJobtype, action.transactiontype);
        const jobtypes = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: 'MODIFY_JOBTYPE_RECEIVE', jobtypes });
    } catch (error) {
        yield put({ type: 'MODIFY_JOBTYPE_FAILURE', error });
    }
}


// watcher saga
export function* requestCreateJobtypeSaga() {
    yield takeLatest("CREATE_JOBTYPE_REQUEST", receiveCreateJobtypeSaga);
}

function doCreateJobtype(jobtype) {
    return axios.post('/ukelonn/api/admin/jobtype/create', jobtype);
}

// worker saga
function* receiveCreateJobtypeSaga(action) {
    try {
        const response = yield call(doCreateJobtype, action.transactiontype);
        const jobtypes = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: 'CREATE_JOBTYPE_RECEIVE', jobtypes });
    } catch (error) {
        yield put({ type: 'CREATE_JOBTYPE_FAILURE', error });
    }
}


// watcher saga
export function* requestDeleteJobsSaga() {
    yield takeLatest("DELETE_JOBS_REQUEST", receiveDeleteJobsSaga);
}

function doDeleteJobs(accountWithJobIds) {
    return axios.post('/ukelonn/api/admin/jobs/delete', accountWithJobIds);
}

// worker saga
function* receiveDeleteJobsSaga(action) {
    try {
        const idsOfJobsToBeDeleted = action.jobsToDelete.map((job) => { return job.id; });
        const response = yield call(doDeleteJobs, { account: action.account, jobIds: idsOfJobsToBeDeleted });
        const jobs = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: 'DELETE_JOBS_RECEIVE', jobs });
    } catch (error) {
        yield put({ type: 'DELETE_JOBS_FAILURE', error });
    }
}


// watcher saga
export function* requestUpdateJobSaga() {
    yield takeLatest("UPDATE_JOB_REQUEST", receiveUpdateJobSaga);
}

function doUpdateJob(updatedJob) {
    return axios.post('/ukelonn/api/job/update', updatedJob);
}

// worker saga
function* receiveUpdateJobSaga(action) {
    try {
        const response = yield call(doUpdateJob, { ...action.selectedjob });
        const jobs = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: 'UPDATE_JOB_RECEIVE', jobs });
    } catch (error) {
        yield put({ type: 'UPDATE_JOB_FAILURE', error });
    }
}


// watcher saga
export function* requestModifyPaymenttypeSaga() {
    yield takeLatest("MODIFY_PAYMENTTYPE_REQUEST", receiveModifyPaymenttypeSaga);
}

function doModifyPaymenttype(paymenttype) {
    return axios.post('/ukelonn/api/admin/paymenttype/modify', paymenttype);
}

// worker saga
function* receiveModifyPaymenttypeSaga(action) {
    try {
        const response = yield call(doModifyPaymenttype, action.transactiontype);
        const paymenttypes = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: 'MODIFY_PAYMENTTYPE_RECEIVE', paymenttypes });
    } catch (error) {
        yield put({ type: 'MODIFY_PAYMENTTYPE_FAILURE', error });
    }
}


// watcher saga
export function* requestCreatePaymenttypeSaga() {
    yield takeLatest("CREATE_PAYMENTTYPE_REQUEST", receiveCreatePaymenttypeSaga);
}

function doCreatePaymenttype(paymenttype) {
    return axios.post('/ukelonn/api/admin/paymenttype/create', paymenttype);
}

// worker saga
function* receiveCreatePaymenttypeSaga(action) {
    try {
        const response = yield call(doCreatePaymenttype, action.transactiontype);
        const paymenttypes = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: 'CREATE_PAYMENTTYPE_RECEIVE', paymenttypes });
    } catch (error) {
        yield put({ type: 'CREATE_PAYMENTTYPE_FAILURE', error });
    }
}


// watcher saga
export function* requestUsersSaga() {
    yield takeLatest("USERS_REQUEST", receiveUsersSaga);
}

function doUsers() {
    return axios.get('/ukelonn/api/users');
}

// worker saga
function* receiveUsersSaga(action) {
    try {
        const response = yield call(doUsers);
        const users = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: 'USERS_RECEIVE', users: users });
    } catch (error) {
        yield put({ type: 'USERS_FAILURE', error });
    }
}


// watcher saga
export function* requestModifyUserSaga() {
    yield takeLatest("MODIFY_USER_REQUEST", receiveModifyUserSaga);
}

function doModifyUser(user) {
    return axios.post('/ukelonn/api/admin/user/modify', user);
}

// worker saga
function* receiveModifyUserSaga(action) {
    try {
        const response = yield call(doModifyUser, action.user);
        const users = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: 'MODIFY_USER_RECEIVE', users });
    } catch (error) {
        yield put({ type: 'MODIFY_USER_FAILURE', error });
    }
}


// watcher saga
export function* requestCreateUserSaga() {
    yield takeLatest("CREATE_USER_REQUEST", receiveCreateUserSaga);
}

function doCreateUser(passwords) {
    return axios.post('/ukelonn/api/admin/user/create', passwords);
}

// worker saga
function* receiveCreateUserSaga(action) {
    try {
        const passwords = {...action.passwords, user: {...action.user}};
        const response = yield call(doCreateUser, passwords);
        const users = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: 'CREATE_USER_RECEIVE', users });
    } catch (error) {
        yield put({ type: 'CREATE_USER_FAILURE', error });
    }
}


// watcher saga
export function* requestChangePasswordSaga() {
    yield takeLatest("MODIFY_USER_PASSWORD_REQUEST", receiveChangePasswordSaga);
}

function doChangePassword(passwords) {
    return axios.post('/ukelonn/api/admin/user/password', passwords);
}

// worker saga
function* receiveChangePasswordSaga(action) {
    try {
        const passwords = {...action.passwords, user: {...action.user}};
        const response = yield call(doChangePassword, passwords);
        const users = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put({ type: 'MODIFY_USER_PASSWORD_RECEIVE', users });
    } catch (error) {
        yield put({ type: 'MODIFY_USER_PASSWORD_FAILURE', error });
    }
}


// watcher saga
export function* startNotificationListening() {
    yield takeLatest("START_NOTIFICATION_LISTENING", pollNotification);
}

// worker saga
function* pollNotification(action) {
    const notificationsRestEndpoint = '/ukelonn/api/notificationsto/' + action.username;
    var loop = true;
    try {
        while (loop) {
            const response = yield call(() => axios({ url: notificationsRestEndpoint }));
            if (response.headers["content-type"] === "application/json" && response.data.length > 0) {
                yield put({ type: 'RECEIVED_NOTIFICATION', notifications: response.data });
            }

            if (response.headers["content-type"] === "text/html") { // Happens in redirect to login page after logout
                loop = false;
            }

            yield call(delay, 60000);
        }
    } catch (err) {
        // Error will break the loop
        yield put({ type: 'ERROR_RECEIVED_NOTIFICATION', err });
    }
}


export function* rootSaga() {
    yield [
        fork(requestInitialLoginStateSaga),
        fork(requestLoginSaga),
        fork(requestLogoutSaga),
        fork(requestAccountSaga),
        fork(requestJobtypeListSaga),
        fork(requestPaymenttypeListSaga),
        fork(requestRegisterJobSaga),
        fork(requestRecentJobsSaga),
        fork(requestRecentPaymentsSaga),
        fork(requestAccountsSaga),
        fork(requestPaymenttypesSaga),
        fork(requestRegisterPaymentSaga),
        fork(requestModifyJobtypeSaga),
        fork(requestCreateJobtypeSaga),
        fork(requestDeleteJobsSaga),
        fork(requestUpdateJobSaga),
        fork(requestModifyPaymenttypeSaga),
        fork(requestCreatePaymenttypeSaga),
        fork(requestUsersSaga),
        fork(requestModifyUserSaga),
        fork(requestCreateUserSaga),
        fork(requestChangePasswordSaga),
        fork(startNotificationListening),
    ];
};
