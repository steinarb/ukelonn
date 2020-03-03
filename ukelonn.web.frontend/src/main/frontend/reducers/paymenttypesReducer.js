import { createReducer } from '@reduxjs/toolkit';
import {
    PAYMENTTYPELIST_RECEIVE,
    PAYMENTTYPES_RECEIVE,
    MODIFY_PAYMENTTYPE_RECEIVE,
    CREATE_PAYMENTTYPE_RECEIVE,
} from '../actiontypes';

const emptyPaymenttype = {
    id: -1,
    transactionName: '',
    transactionAmount: 0.0
};

const paymenttypesReducer = createReducer([], {
    [PAYMENTTYPELIST_RECEIVE]: (state, action) => action.payload,
    [PAYMENTTYPES_RECEIVE]: (state, action) => addEmptyPaymenttypeToReceivedList(state, action),
    [MODIFY_PAYMENTTYPE_RECEIVE]: (state, action) => action.payload,
    [CREATE_PAYMENTTYPE_RECEIVE]: (state, action) => action.paymenttypes,
});

export default paymenttypesReducer;

function addEmptyPaymenttypeToReceivedList(state, action) {
    const paymenttypes = action.payload || [];
    if (!paymenttypes.find((payment) => payment.id === -1)) {
        state.paymenttypes.unshift(emptyPaymenttype);
    }

    return paymenttypes;
}
