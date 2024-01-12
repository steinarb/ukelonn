import { createReducer } from '@reduxjs/toolkit';
import {
    RECENTJOBS_RECEIVE,
    DELETE_JOBS_RECEIVE,
    UPDATE_JOB_RECEIVE,
    MODIFY_MARK_JOB_FOR_DELETE,
} from '../actiontypes';
const defaultValue = [];

const paymenttypeReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(RECENTJOBS_RECEIVE, (state, action) => action.payload)
        .addCase(DELETE_JOBS_RECEIVE, (state, action) => action.payload)
        .addCase(UPDATE_JOB_RECEIVE, (state, action) => action.payload)
        .addCase(MODIFY_MARK_JOB_FOR_DELETE, (state, action) => state.map(j => j.id === action.payload.id ? action.payload : j));
});

export default paymenttypeReducer;
