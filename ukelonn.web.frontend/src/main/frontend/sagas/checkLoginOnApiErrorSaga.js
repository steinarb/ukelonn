import { takeLatest, debounce, put } from 'redux-saga/effects';
import { push } from 'redux-first-history';
import {
    REST_API_FAILURE_UNAUTHORIZED,
    REST_API_FAILURE_FORBIDDEN,
    CHECK_LOGIN_STATE_REQUEST,
    ACCOUNT_FAILURE,
    JOBTYPELIST_FAILURE,
    REGISTERJOB_FAILURE,
    RECENTJOBS_FAILURE,
    RECENTPAYMENTS_FAILURE,
    ACCOUNTS_FAILURE,
    PAYMENTTYPES_FAILURE,
    REGISTERPAYMENT_FAILURE,
    MODIFY_JOBTYPE_FAILURE,
    CREATE_JOBTYPE_FAILURE,
    DELETE_JOBS_FAILURE,
    UPDATE_JOB_FAILURE,
    MODIFY_PAYMENTTYPE_FAILURE,
    CREATE_PAYMENTTYPE_FAILURE,
    USERS_FAILURE,
    CHANGE_USER_FAILURE,
    CREATE_USER_FAILURE,
    CHANGE_USER_PASSWORD_FAILURE,
    RECEIVE_ACTIVE_BONUSES_FAILURE,
    RECEIVE_ALL_BONUSES_FAILURE,
    CREATE_BONUS_FAILURE,
    MODIFY_BONUS_FAILURE,
    DELETE_BONUS_FAILURE,
    EARNINGS_SUM_OVER_YEAR_FAILURE,
    EARNINGS_SUM_OVER_MONTH_FAILURE,
} from '../actiontypes';

export default function* checkLoginOnApiErrorSaga() {
    yield takeLatest(REST_API_FAILURE_UNAUTHORIZED, checkLoginState);
    yield takeLatest(REST_API_FAILURE_FORBIDDEN, checkLoginState);
    yield takeLatest(REST_API_FAILURE_UNAUTHORIZED, goToLogin);
    yield takeLatest(REST_API_FAILURE_FORBIDDEN, goToUnauthorized);
    yield debounce(1000, [
        ACCOUNT_FAILURE,
        JOBTYPELIST_FAILURE,
        REGISTERJOB_FAILURE,
        RECENTJOBS_FAILURE,
        RECENTPAYMENTS_FAILURE,
        ACCOUNTS_FAILURE,
        PAYMENTTYPES_FAILURE,
        REGISTERPAYMENT_FAILURE,
        MODIFY_JOBTYPE_FAILURE,
        CREATE_JOBTYPE_FAILURE,
        DELETE_JOBS_FAILURE,
        UPDATE_JOB_FAILURE,
        MODIFY_PAYMENTTYPE_FAILURE,
        CREATE_PAYMENTTYPE_FAILURE,
        USERS_FAILURE,
        CHANGE_USER_FAILURE,
        CREATE_USER_FAILURE,
        CHANGE_USER_PASSWORD_FAILURE,
        RECEIVE_ACTIVE_BONUSES_FAILURE,
        RECEIVE_ALL_BONUSES_FAILURE,
        CREATE_BONUS_FAILURE,
        MODIFY_BONUS_FAILURE,
        DELETE_BONUS_FAILURE,
        EARNINGS_SUM_OVER_YEAR_FAILURE,
        EARNINGS_SUM_OVER_MONTH_FAILURE,
    ], finnTypeFeil);
}

function* finnTypeFeil(action) {
    const responseCode = action.payload.response.status;
    if (responseCode === 401) {
        yield put(REST_API_FAILURE_UNAUTHORIZED());
    } else if (responseCode === 403) {
        yield put(REST_API_FAILURE_FORBIDDEN());
    }
}

function* checkLoginState() {
    yield put(CHECK_LOGIN_STATE_REQUEST());
}

function* goToLogin() {
    yield put(push('/login'));
}

function* goToUnauthorized() {
    yield put(push('/unauthorized'));
}
