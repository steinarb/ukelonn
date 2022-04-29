import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    MODIFY_PAYMENTTYPE_REQUEST,
    MODIFY_PAYMENTTYPE_RECEIVE,
    MODIFY_PAYMENTTYPE_FAILURE,
} from '../actiontypes';

function doModifyPaymenttype(paymenttype) {
    return axios.post('/ukelonn/api/admin/paymenttype/modify', paymenttype);
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

export default function* modifyPaymenttypeSaga() {
    yield takeLatest(MODIFY_PAYMENTTYPE_REQUEST, requestReceiveModifyPaymenttypeSaga);
}
