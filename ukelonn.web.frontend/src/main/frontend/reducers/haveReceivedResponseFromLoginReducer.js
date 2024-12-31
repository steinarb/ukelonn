import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_FIRSTTIMEAFTERLOGIN,
} from '../actiontypes';
import { api } from '../api';

const haveReceivedResponseFromLoginReducer = createReducer(false, builder => {
    builder
        .addCase(UPDATE_FIRSTTIMEAFTERLOGIN, (state, action) => action.payload ? true : state)
        .addMatcher(api.endpoints.postLogin.matchFulfilled, () => true)
        .addMatcher(api.endpoints.postLogout.matchFulfilled, () => true)
        .addMatcher(api.endpoints.getLogin.matchFulfilled, () => true);
});

export default haveReceivedResponseFromLoginReducer;
