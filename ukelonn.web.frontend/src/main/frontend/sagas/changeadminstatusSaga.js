import { takeLatest, call, put, fork } from 'redux-saga/effects';
import axios from 'axios';
import {
    CHANGE_ADMIN_STATUS,
    CHANGE_ADMIN_STATUS_RESPONSE,
    CHANGE_ADMIN_STATUS_ERROR,
} from '../actiontypes';

export default function* () {
    yield takeLatest(CHANGE_ADMIN_STATUS, changeAdminStatus);
}

function* changeAdminStatus(action) {
    const adminStatus = action.payload;
    try {
        const response = yield call(fetchAdminStatus, adminStatus);
        const changedAdminStatus = (response.headers['content-type'] === 'application/json') ? response.data : {};
        yield put(CHANGE_ADMIN_STATUS_RESPONSE(changedAdminStatus));
    } catch (error) {
        yield put(CHANGE_ADMIN_STATUS_ERROR(error));
    }
}

function fetchAdminStatus(user) {
    return axios.post('/ukelonn/api/admin/user/changeadminstatus', user);
}
