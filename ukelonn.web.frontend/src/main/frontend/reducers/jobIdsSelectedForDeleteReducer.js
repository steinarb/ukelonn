import { createReducer } from '@reduxjs/toolkit';
import { MODIFY_MARK_JOB_FOR_DELETE } from '../actiontypes';
import { api } from '../api';
import { isJobsLoaded } from '../matchers';
const defaultValue = [];

const jobIdsSelectedForDeleteReducer = createReducer(defaultValue, builder => {
    builder
        .addMatcher(isJobsLoaded, () => defaultValue)
        .addCase(MODIFY_MARK_JOB_FOR_DELETE, (state, action) => action.payload.delete ? [...state, action.payload.id] : state.filter(j => j !== action.payload.id));
});

export default jobIdsSelectedForDeleteReducer;
