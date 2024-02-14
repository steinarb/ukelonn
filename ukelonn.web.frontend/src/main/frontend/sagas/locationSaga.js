import { takeLatest, put, select } from 'redux-saga/effects';
import { LOCATION_CHANGE } from 'redux-first-history';
import { parse } from 'qs';
import {
    ACCOUNT_REQUEST,
    ACCOUNTS_REQUEST,
    CLEAR_USER_AND_PASSWORDS,
    USERS_REQUEST,
    PAYMENTTYPES_REQUEST,
    CLEAR_PAYMENT_TYPE_FORM,
    JOBTYPELIST_REQUEST,
    RECENTJOBS_REQUEST,
    RECENTPAYMENTS_REQUEST,
    GET_ACTIVE_BONUSES,
    GET_ALL_BONUSES,
    CLEAR_BONUS,
} from '../actiontypes';
import { findUsername } from '../common/login';

function* locationChange(action) {
    const { location = {} } = action.payload || {};
    const basename = yield select(state => state.router.basename);
    const pathname = findPathname(location, basename);

    if (pathname === '/user') {
        const username = yield select(findUsername);
        yield put(ACCOUNT_REQUEST(username));
        yield put(JOBTYPELIST_REQUEST());
        yield put(GET_ACTIVE_BONUSES());
    }

    if (pathname === '/performedjobs') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { username, accountId } = queryParams;
        yield put(ACCOUNT_REQUEST(username));
        yield put(RECENTJOBS_REQUEST(accountId));
    }

    if (pathname === '/performedpayments') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { username, accountId } = queryParams;
        yield put(ACCOUNT_REQUEST(username));
        yield put(RECENTPAYMENTS_REQUEST(accountId));
    }

    if (pathname === '/statistics/earnings/sumoveryear' || pathname === '/statistics/earnings/sumovermonth') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { username } = queryParams;
        yield put(ACCOUNT_REQUEST(username));
    }

    if (pathname === '/admin') {
        yield put(ACCOUNTS_REQUEST());
        yield put(PAYMENTTYPES_REQUEST());
        yield put(GET_ACTIVE_BONUSES());
    }

    if (pathname === '/admin/jobtypes/modify' || pathname === '/admin/jobtypes/create') {
        yield put(JOBTYPELIST_REQUEST());
    }

    if (pathname === '/admin/jobs/delete') {
        yield put(ACCOUNTS_REQUEST());
        const accountId = yield select(state => state.accountId);
        yield put(RECENTJOBS_REQUEST(accountId));
    }

    if (pathname === '/admin/jobs/edit') {
        yield put(ACCOUNTS_REQUEST());
        yield put(JOBTYPELIST_REQUEST());
        const accountId = yield select(state => state.accountId);
        yield put(RECENTJOBS_REQUEST(accountId));
    }

    if (pathname === '/admin/paymenttypes/modify' || pathname === '/admin/paymenttypes/create') {
        yield put((PAYMENTTYPES_REQUEST()));
        yield put((CLEAR_PAYMENT_TYPE_FORM()));
    }

    if (pathname === '/admin/users/modify' || pathname === '/admin/users/password' || pathname === '/admin/users/create') {
        yield put(USERS_REQUEST());
        yield put(CLEAR_USER_AND_PASSWORDS());
    }

    if (pathname === '/admin/bonuses/create') {
        yield put(CLEAR_BONUS());
    }

    if (pathname === '/admin/bonuses/modify') {
        yield put(GET_ALL_BONUSES());
        yield put(CLEAR_BONUS());
    }

    if (pathname === '/admin/bonuses/delete') {
        yield put(GET_ALL_BONUSES());
        yield put(CLEAR_BONUS());
    }
}

export default function* locationSaga() {
    yield takeLatest(LOCATION_CHANGE, locationChange);
}

function findPathname(location, basename) {
    if (basename === '/') {
        return location.pathname;
    }

    return location.pathname.replace(new RegExp('^' + basename + '(.*)'), '$1');
}
