import { createReducer } from '@reduxjs/toolkit';
import {
    USERS_RECEIVE,
    CHANGE_USER_RECEIVE,
    CREATE_USER_RECEIVE,
    CHANGE_USER_PASSWORD_RECEIVE,
} from '../actiontypes';

const usersReducer = createReducer([], {
    [USERS_RECEIVE]: (state, action) => action.payload,
    [CHANGE_USER_RECEIVE]: (state, action) => action.payload,
    [CREATE_USER_RECEIVE]: (state, action) => action.payload,
    [CHANGE_USER_PASSWORD_RECEIVE]: (state, action) => action.payload,
});

export default usersReducer;
