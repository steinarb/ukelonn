import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_FIRSTTIMEAFTERLOGIN,
    LOGIN_RECEIVE,
    LOGOUT_RECEIVE,
    INITIAL_LOGIN_STATE_RECEIVE,
} from '../actiontypes';

const haveReceivedResponseFromLoginReducer = createReducer(false, {
    [UPDATE_FIRSTTIMEAFTERLOGIN]: (state, action) => action.payload ? true : state,
    [LOGIN_RECEIVE]: () => true,
    [LOGOUT_RECEIVE]: () => true,
    [INITIAL_LOGIN_STATE_RECEIVE]: () => true,
});

export default haveReceivedResponseFromLoginReducer;
