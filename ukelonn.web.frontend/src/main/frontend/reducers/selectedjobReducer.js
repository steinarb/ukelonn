import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_ACCOUNT,
    UPDATE_SELECTEDJOB,
    UPDATE_JOB_RECEIVE,
} from '../actiontypes';
import { emptyTransaction } from './constants';

const selectedjobReducer = createReducer({ ...emptyTransaction }, {
    [UPDATE_ACCOUNT]: (state, action) => ({ ...state, accountId: action.payload.accountId }),
    [UPDATE_SELECTEDJOB]: (state, action) => ({ ...state, ...action.payload }),
    [UPDATE_JOB_RECEIVE]: (state, action) => ({ ...state, ...emptyTransaction, transactionTypeId: -1 }),
});

export default selectedjobReducer;
