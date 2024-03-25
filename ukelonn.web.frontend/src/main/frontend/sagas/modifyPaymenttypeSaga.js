import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    MODIFY_PAYMENTTYPE_REQUEST,
    MODIFY_PAYMENTTYPE_RECEIVE,
    MODIFY_PAYMENTTYPE_FAILURE,
} from '../actiontypes';

export default function* modifyPaymenttypeSaga() {
    yield takeLatest(MODIFY_PAYMENTTYPE_REQUEST, requestReceiveModifyPaymenttypeSaga);
}

function* requestReceiveModifyPaymenttypeSaga(action) {
    try {
        const response = yield call(doModifyPaymenttype, action.payload);
        const paymenttypes = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(MODIFY_PAYMENTTYPE_RECEIVE(paymenttypes));
    } catch (error) {
        yield put(MODIFY_PAYMENTTYPE_FAILURE(error));
    }
}

function doModifyPaymenttype(paymenttype) {
    return axios.post('/api/admin/paymenttype/modify', paymenttype);
}
