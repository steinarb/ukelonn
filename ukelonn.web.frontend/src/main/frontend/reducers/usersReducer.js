import { createReducer } from '@reduxjs/toolkit';
import {
    USERS_RECEIVE,
    CHANGE_USER_RECEIVE,
    CREATE_USER_RECEIVE,
    CHANGE_USER_PASSWORD_RECEIVE,
} from '../actiontypes';

const usersReducer = createReducer([], builder => {
    builder
        .addCase(USERS_RECEIVE, (state, action) => action.payload)
        .addCase(CHANGE_USER_RECEIVE, (state, action) => action.payload)
        .addCase(CREATE_USER_RECEIVE, (state, action) => action.payload)
        .addCase(CHANGE_USER_PASSWORD_RECEIVE, (state, action) => action.payload);
});

export default usersReducer;
