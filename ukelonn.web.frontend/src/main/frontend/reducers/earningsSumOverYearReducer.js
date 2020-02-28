import { createReducer } from '@reduxjs/toolkit';
import {
    EARNINGS_SUM_OVER_YEAR_RECEIVE,
} from '../actiontypes';

const accountReducer = createReducer([], {
    [EARNINGS_SUM_OVER_YEAR_RECEIVE]: (state, action) => action.payload,
});

export default accountReducer;
