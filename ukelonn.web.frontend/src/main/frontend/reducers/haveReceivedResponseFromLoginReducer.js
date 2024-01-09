import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_FIRSTTIMEAFTERLOGIN,
    LOGIN_RECEIVE,
    LOGOUT_RECEIVE,
    INITIAL_LOGIN_STATE_RECEIVE,
} from '../actiontypes';

const haveReceivedResponseFromLoginReducer = createReducer(false, builder => {
    builder
        .addCase(UPDATE_FIRSTTIMEAFTERLOGIN, (state, action) => action.payload ? true : state)
        .addCase(LOGIN_RECEIVE, () => true)
        .addCase(LOGOUT_RECEIVE, () => true)
        .addCase(INITIAL_LOGIN_STATE_RECEIVE, () => true);
});

export default haveReceivedResponseFromLoginReducer;
