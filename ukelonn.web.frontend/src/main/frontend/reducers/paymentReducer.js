import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE,
    REGISTERPAYMENT_RECEIVE,
} from '../actiontypes';
import { emptyPerformedTransaction } from './constants';

const paymentReducer = createReducer({ ...emptyPerformedTransaction }, {
    [UPDATE]: (state, action) => {
        if (!action.payload) { return state; }
        const payment = action.payload.payment;
        if (payment === undefined) { return state; }
        return { ...state, ...payment };
    },
    [REGISTERPAYMENT_RECEIVE]: (state, action) => ({ ...emptyPerformedTransaction }),
});

export default paymentReducer;
