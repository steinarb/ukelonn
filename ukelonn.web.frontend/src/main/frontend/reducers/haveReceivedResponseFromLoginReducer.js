import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_FIRSTTIMEAFTERLOGIN,
    LOGIN_RECEIVE,
    LOGOUT_RECEIVE,
    INITIAL_LOGIN_STATE_RECEIVE,
} from '../actiontypes';

const haveReceivedResponseFromLoginReducer = createReducer(false, {
    [UPDATE_FIRSTTIMEAFTERLOGIN]: (state, action) => action.payload ? true : state,
    [LOGIN_RECEIVE]: (state, action) => true,
    [LOGOUT_RECEIVE]: (state, action) => true,
    [INITIAL_LOGIN_STATE_RECEIVE]: (state, action) => true,
});

export default haveReceivedResponseFromLoginReducer;
