import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
    LOGOUT_RECEIVE,
    INITIAL_LOGIN_STATE_RECEIVE,
} from '../actiontypes';

const loginResponse = createReducer({ username: '', roles: [],error: '' }, builder => {
    builder
        .addCase(LOGIN_RECEIVE, (state, action) => action.payload)
        .addCase(LOGOUT_RECEIVE, (state, action) => action.payload)
        .addCase(INITIAL_LOGIN_STATE_RECEIVE, (state, action) => action.payload);
});

export default loginResponse;
