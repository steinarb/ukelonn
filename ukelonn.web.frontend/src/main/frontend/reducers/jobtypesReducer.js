import { createReducer } from '@reduxjs/toolkit';
import {
    JOBTYPELIST_RECEIVE,
    MODIFY_JOBTYPE_RECEIVE,
    CREATE_JOBTYPE_RECEIVE,
} from '../actiontypes';

const jobtypesReducer = createReducer([], builder => {
    builder
        .addCase(JOBTYPELIST_RECEIVE, (state, action) => action.payload)
        .addCase(MODIFY_JOBTYPE_RECEIVE, (state, action) => action.payload)
        .addCase(CREATE_JOBTYPE_RECEIVE, (state, action) => action.payload);
});

export default jobtypesReducer;
