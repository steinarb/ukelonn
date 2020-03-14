import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_PASSWORD,
    LOGIN_RECEIVE,
    INITIAL_LOGIN_STATE_RECEIVE,
} from '../actiontypes';

const passwordReducer = createReducer(null, {
    [UPDATE_PASSWORD]: (state, action) => action.payload,
    [LOGIN_RECEIVE]: (state, action) => '',
    [INITIAL_LOGIN_STATE_RECEIVE]: (state, action) => '',
});

export default passwordReducer;
