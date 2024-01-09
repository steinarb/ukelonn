import { createReducer } from '@reduxjs/toolkit';
import {
    RECEIVE_ACTIVE_BONUSES,
} from '../actiontypes';

const activebonusesReducer = createReducer([], builder => {
    builder
        .addCase(RECEIVE_ACTIVE_BONUSES, (state, action) => action.payload);
});

export default activebonusesReducer;
