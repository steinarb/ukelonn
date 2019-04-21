import { createReducer } from 'redux-starter-kit';
import {
    LOGIN_RECEIVE,
    LOGOUT_RECEIVE,
    INITIAL_LOGIN_STATE_RECEIVE,
} from '../actiontypes';

const loginResponse = createReducer({ username: '', roles: [],error: '' }, {
    [LOGIN_RECEIVE]: (state, action) => action.payload,
    [LOGOUT_RECEIVE]: (state, action) => action.payload,
    [INITIAL_LOGIN_STATE_RECEIVE]: (state, action) => action.payload,
});

export default loginResponse;
