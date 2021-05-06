import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_BONUS,
} from '../actiontypes';

const bonusReducer = createReducer({}, {
    [UPDATE_BONUS]: (state, action) => ({ ...state, ...action.payload }),
});

export default bonusReducer;
