import { takeLatest, put, select } from 'redux-saga/effects';
import {
    SELECT_PAYMENT_TYPE,
    SELECT_PAYMENT_TYPE_FOR_EDIT,
    SELECTED_PAYMENT_TYPE,
    MODIFY_PAYMENT_AMOUNT,
    REGISTER_PAYMENT_BUTTON_CLICKED,
    REGISTERPAYMENT_REQUEST,
    CLEAR_PAYMENT_TYPE_FORM,
} from '../actiontypes';

function* selectPaymentType(action) {
    const transactionTypeId = parseInt(action.payload);
    if (transactionTypeId === -1) {
        const balance = yield select(state => state.accountBalance);
        yield put(MODIFY_PAYMENT_AMOUNT(balance));
    }
    const paymenttypes = yield select(state => state.paymenttypes);
    const paymenttype = paymenttypes.find(p => p.id === transactionTypeId);
    if (paymenttype && paymenttype.transactionAmount > 0) {
        yield put(MODIFY_PAYMENT_AMOUNT(paymenttype.transactionAmount));
    } else {
        const balance = yield select(state => state.accountBalance);
        yield put(MODIFY_PAYMENT_AMOUNT(balance));
    }
}

function* selectPaymentTypeForEdit(action) {
    const transactionTypeId = action.payload;
    if (transactionTypeId === -1) {
        yield put(CLEAR_PAYMENT_TYPE_FORM());
    }
    const paymenttypes = yield select(state => state.paymenttypes);
    const paymenttype = paymenttypes.find(p => p.id === transactionTypeId);
    if (paymenttype) {
        yield put(SELECTED_PAYMENT_TYPE(paymenttype));
    }
}

function* buildRequestAndRegisterPayment() {
    const payment = yield select(state => ({
        account: {
            accountId: state.accountId,
            username: state.accountUsername,
        },
        transactionTypeId: state.transactionTypeId,
        transactionAmount: state.transactionAmount,
    }));
    yield put(REGISTERPAYMENT_REQUEST(payment));
}

export default function* paymentSaga() {
    yield takeLatest(SELECT_PAYMENT_TYPE, selectPaymentType);
    yield takeLatest(SELECT_PAYMENT_TYPE_FOR_EDIT, selectPaymentTypeForEdit);
    yield takeLatest(REGISTER_PAYMENT_BUTTON_CLICKED, buildRequestAndRegisterPayment);
}
