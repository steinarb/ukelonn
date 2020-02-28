import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE,
    LOGIN_RECEIVE,
    INITIAL_LOGIN_STATE_RECEIVE,
} from '../actiontypes';

const passwordReducer = createReducer(null, {
    [UPDATE]: (state, action) => {
        if (!(action.payload && action.payload.password)) {
            return state;
        }
        return action.payload.password;
    },
    [LOGIN_RECEIVE]: (state, action) => '',
    [INITIAL_LOGIN_STATE_RECEIVE]: (state, action) => '',
});

export default passwordReducer;
