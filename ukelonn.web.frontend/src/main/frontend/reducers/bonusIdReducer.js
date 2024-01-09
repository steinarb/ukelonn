import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_BONUS,
    CLEAR_BONUS,
} from '../actiontypes';
const unselected = -1;

const bonusIdReducer = createReducer(unselected, builder => {
    builder
        .addCase(SELECT_BONUS, (state, action) => action.payload)
        .addCase(CLEAR_BONUS, () => unselected);
});

export default bonusIdReducer;
