import { createReducer } from 'redux-starter-kit';
import {
    UPDATE,
    PAYMENTTYPES_RECEIVE,
} from '../actiontypes';
import { emptyPerformedTransaction } from './constants';

const paymenttypeReducer = createReducer({ id: -1, transactionTypeName: '', transactionAmount: 0.0, transactionIsWork: false, transactionIsWagePayment: true }, {
    [UPDATE]: (state, action) => {
        if (!action.payload) { return state; }
        const paymenttype = action.payload.paymenttype;
        if (paymenttype === undefined) { return state; }
        return { ...state, ...paymenttype };
    },
    [PAYMENTTYPES_RECEIVE]: (state, action) => action.payload[0] || { ...emptyPerformedTransaction },
});

export default paymenttypeReducer;
