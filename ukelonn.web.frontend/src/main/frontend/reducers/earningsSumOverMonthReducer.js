import { createReducer } from '@reduxjs/toolkit';
import {
    EARNINGS_SUM_OVER_MONTH_RECEIVE,
    INITIAL_LOGIN_STATE_RECEIVE,
    LOGIN_RECEIVE,
} from '../actiontypes';
import { isAdmin } from '../common/roles';

const accountReducer = createReducer([], builder => {
    builder
        .addCase(EARNINGS_SUM_OVER_MONTH_RECEIVE, (state, action) => action.payload)
        .addCase(INITIAL_LOGIN_STATE_RECEIVE, (state, action) => isAdmin(action) ? [] : state)
        .addCase(LOGIN_RECEIVE, (state, action) => isAdmin(action) ? [] : state);
});

export default accountReducer;
