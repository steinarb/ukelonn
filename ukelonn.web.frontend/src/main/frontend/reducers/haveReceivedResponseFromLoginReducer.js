import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE,
    LOGIN_RECEIVE,
    LOGOUT_RECEIVE,
    INITIAL_LOGIN_STATE_RECEIVE,
} from '../actiontypes';

const haveReceivedResponseFromLoginReducer = createReducer(false, {
    [UPDATE]: (state, action) => {
        if (!action.payload) { return state; }
        const firstTimeAfterLogin = action.payload.firstTimeAfterLogin;
        if (firstTimeAfterLogin === undefined) { return state; }
        return firstTimeAfterLogin;
    },
    [LOGIN_RECEIVE]: (state, action) => true,
    [LOGOUT_RECEIVE]: (state, action) => true,
    [INITIAL_LOGIN_STATE_RECEIVE]: (state, action) => true,
});

export default haveReceivedResponseFromLoginReducer;
