import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_ACCOUNT,
    UPDATE_PAYMENT,
    REGISTERPAYMENT_RECEIVE,
    INITIAL_LOGIN_STATE_RECEIVE,
    LOGIN_RECEIVE,
} from '../actiontypes';
import { bankAccount, emptyPerformedTransaction } from './constants';
import { isAdmin } from '../common/roles';

const paymentReducer = createReducer({ ...emptyPerformedTransaction }, {
    [UPDATE_ACCOUNT]: (state, action) => ({ ...state, transactionTypeId: bankAccount, transactionAmount: action.payload.balance, account: { ...action.payload } }),
    [UPDATE_PAYMENT]: (state, action) => ({ ...state, ...action.payload }),
    [REGISTERPAYMENT_RECEIVE]: (state, action) => ({ ...emptyPerformedTransaction }),
    [INITIAL_LOGIN_STATE_RECEIVE]: (state, action) => isAdmin(action) ? { ...emptyPerformedTransaction } : state,
    [LOGIN_RECEIVE]: (state, action) => isAdmin(action) ? { ...emptyPerformedTransaction } : state,
});

export default paymentReducer;
