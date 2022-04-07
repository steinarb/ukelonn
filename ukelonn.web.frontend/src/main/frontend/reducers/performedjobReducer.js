import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    UPDATE_PERFORMEDJOB,
    REGISTERJOB_RECEIVE,
} from '../actiontypes';
import { emptyPerformedTransaction } from './constants';

const performedjobReducer = createReducer({ ...emptyPerformedTransaction }, {
    [ACCOUNT_RECEIVE]: (state, action) => ({ ...state, account: { ...action.payload } }),
    [UPDATE_PERFORMEDJOB]: (state, action) => ({ ...state, ...action.payload }),
    [REGISTERJOB_RECEIVE]: (state, action) => ({ ...emptyPerformedTransaction, transactionDate: new Date().toISOString(), account: { ...action.payload } }),
});

export default performedjobReducer;
