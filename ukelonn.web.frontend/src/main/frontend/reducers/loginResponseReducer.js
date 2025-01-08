import { createReducer } from '@reduxjs/toolkit';
import { api } from '../api';

const loginResponse = createReducer({ username: '', roles: [],error: '' }, builder => {
    builder
        .addMatcher(api.endpoints.postLogin.matchFulfilled, (state, action) => action.payload)
        .addMatcher(api.endpoints.postLogout.matchFulfilled, (state, action) => action.payload)
        .addMatcher(api.endpoints.getLogin.matchFulfilled, (state, action) => action.payload);
});

export default loginResponse;
