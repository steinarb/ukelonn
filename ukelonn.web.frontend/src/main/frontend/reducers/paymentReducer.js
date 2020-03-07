import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_ACCOUNT,
    UPDATE_PAYMENT,
    REGISTERPAYMENT_RECEIVE,
} from '../actiontypes';
import { bankAccount, emptyPerformedTransaction } from './constants';

const paymentReducer = createReducer({ ...emptyPerformedTransaction }, {
    [UPDATE_ACCOUNT]: (state, action) => ({ ...state, transactionTypeId: bankAccount, transactionAmount: action.payload.balance, account: { ...action.payload } }),
    [UPDATE_PAYMENT]: (state, action) => ({ ...state, ...action.payload }),
    [REGISTERPAYMENT_RECEIVE]: (state, action) => ({ ...emptyPerformedTransaction }),
});

export default paymentReducer;
