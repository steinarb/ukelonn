import { createReducer } from '@reduxjs/toolkit';
import {
    RECEIVE_ALL_BONUSES,
} from '../actiontypes';

const allbonusesReducer = createReducer([], {
    [RECEIVE_ALL_BONUSES]: (state, action) => action.payload,
});

export default allbonusesReducer;
