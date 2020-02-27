import { createReducer } from '@reduxjs/toolkit';
import {
    RECENTJOBS_RECEIVE,
    DELETE_JOBS_RECEIVE,
    UPDATE_JOB_RECEIVE,
} from '../actiontypes';
import { emptyPerformedTransaction } from './constants';

const paymenttypeReducer = createReducer([], {
    [RECENTJOBS_RECEIVE]: (state, action) => action.payload,
    [DELETE_JOBS_RECEIVE]: (state, action) => action.payload,
    [UPDATE_JOB_RECEIVE]: (state, action) => action.payload,
});

export default paymenttypeReducer;
