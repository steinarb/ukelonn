import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_BONUS,
    RECEIVE_ALL_BONUSES,
} from '../actiontypes';

const bonusReducer = createReducer({}, {
    [UPDATE_BONUS]: (state, action) => ({ ...state, ...action.payload }),
});

export default bonusReducer;
