import { createReducer } from '@reduxjs/toolkit';
import {
    RECENTJOBS_RECEIVE,
    DELETE_JOBS_RECEIVE,
    UPDATE_JOB_RECEIVE,
    MODIFY_MARK_JOB_FOR_DELETE,
    CLEAR_ACCOUNT,
} from '../actiontypes';
const defaultValue = [];

const paymenttypeReducer = createReducer(defaultValue, {
    [RECENTJOBS_RECEIVE]: (state, action) => action.payload,
    [DELETE_JOBS_RECEIVE]: (state, action) => action.payload,
    [UPDATE_JOB_RECEIVE]: (state, action) => action.payload,
    [MODIFY_MARK_JOB_FOR_DELETE]: (state, action) => state.map(j => j.id === action.payload.id ? action.payload : j),
    [CLEAR_ACCOUNT]: () => defaultValue,
});

export default paymenttypeReducer;
