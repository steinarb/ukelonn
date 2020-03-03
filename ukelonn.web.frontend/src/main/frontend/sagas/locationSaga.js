import { takeLatest, put, select } from 'redux-saga/effects';
import { LOCATION_CHANGE } from 'connected-react-router';
import { parse } from 'qs';
import {
    ACCOUNT_REQUEST,
    ACCOUNTS_REQUEST,
    USERS_REQUEST,
    PAYMENTTYPES_REQUEST,
    START_NOTIFICATION_LISTENING,
    JOBTYPELIST_REQUEST,
    RECENTJOBS_REQUEST,
    RECENTPAYMENTS_REQUEST,
    UPDATE,
} from '../actiontypes';

function* locationChange(action) {
    const { location = {} } = action.payload || {};
    const { pathname = '', search = '' } = location;

    if (pathname === '/ukelonn/user') {
        const username = yield select(findUsername);
        yield put(ACCOUNT_REQUEST(username));
        yield put(START_NOTIFICATION_LISTENING(username));
        yield put(JOBTYPELIST_REQUEST());
    }

    if (pathname === '/ukelonn/performedjobs') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { username, accountId, parentTitle } = queryParams;
        yield put(ACCOUNT_REQUEST(username));
        yield put(RECENTJOBS_REQUEST(accountId));
        yield put(UPDATE({ parentTitle }));
    }

    if (pathname === '/ukelonn/performedpayments') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { username, accountId, parentTitle } = queryParams;
        yield put(ACCOUNT_REQUEST(username));
        yield put(RECENTPAYMENTS_REQUEST(accountId));
        yield put(UPDATE({ parentTitle }));
    }

    if (pathname === '/ukelonn/statistics/earnings/sumoveryear' || pathname === '/ukelonn/statistics/earnings/sumovermonth') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { username } = queryParams;
        yield put(ACCOUNT_REQUEST(username));
    }

    if (pathname === '/ukelonn/admin') {
        yield put(ACCOUNTS_REQUEST());
        yield put(PAYMENTTYPES_REQUEST());
    }

    if (pathname === '/ukelonn/admin/jobtypes/modify' || pathname === '/ukelonn/admin/jobtypes/create') {
        yield put(JOBTYPELIST_REQUEST());
    }

    if (pathname === '/ukelonn/admin/jobs/delete') {
        yield put(ACCOUNTS_REQUEST());
        const accountId = yield select(findAccountId);
        yield put(RECENTJOBS_REQUEST(accountId));
    }

    if (pathname === '/ukelonn/admin/jobs/edit') {
        yield put(ACCOUNTS_REQUEST());
        yield put(JOBTYPELIST_REQUEST());
        const accountId = yield select(findAccountId);
        yield put(RECENTJOBS_REQUEST(accountId));
    }

    if (pathname === '/ukelonn/admin/paymenttypes/modify' || pathname === '/ukelonn/admin/paymenttypes/create') {
        yield put((PAYMENTTYPES_REQUEST()));
    }

    if (pathname === '/ukelonn/admin/users/modify' || pathname === '/ukelonn/admin/users/password' || pathname === '/ukelonn/admin/users/create') {
        yield put(USERS_REQUEST());
    }
}

export default function* locationSaga() {
    yield takeLatest(LOCATION_CHANGE, locationChange);
}

function findUsername(state) {
    const loginResponse = state.loginResponse || {};
    return loginResponse.username || '';
}

function findAccountId(state) {
    const account = state.accountId || {};
    return account.accountId;
}
