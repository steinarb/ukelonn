import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_JOBS,
    RECENTJOBS_RECEIVE,
    DELETE_JOBS_RECEIVE,
    UPDATE_JOB_RECEIVE,
} from '../actiontypes';

const paymenttypeReducer = createReducer([], {
    [UPDATE_JOBS]: (state, action) => action.payload,
    [RECENTJOBS_RECEIVE]: (state, action) => action.payload,
    [DELETE_JOBS_RECEIVE]: (state, action) => action.payload,
    [UPDATE_JOB_RECEIVE]: (state, action) => action.payload,
});

export default paymenttypeReducer;
