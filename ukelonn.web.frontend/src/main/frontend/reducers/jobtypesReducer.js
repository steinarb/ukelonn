import { createReducer } from '@reduxjs/toolkit';
import {
    JOBTYPELIST_RECEIVE,
    MODIFY_JOBTYPE_RECEIVE,
    CREATE_JOBTYPE_RECEIVE,
} from '../actiontypes';

const jobtypesReducer = createReducer([], {
    [JOBTYPELIST_RECEIVE]: (state, action) => action.payload,
    [MODIFY_JOBTYPE_RECEIVE]: (state, action) => action.payload,
    [CREATE_JOBTYPE_RECEIVE]: (state, action) => action.payload,
});

export default jobtypesReducer;
