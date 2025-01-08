import { createReducer } from '@reduxjs/toolkit';
import {
    SELECTED_BONUS,
    CLEAR_BONUS,
} from '../actiontypes';
import { isAllbonusesLoaded } from '../matchers';
const unselected = -1;

const bonusIdReducer = createReducer(unselected, builder => {
    builder
        .addCase(SELECTED_BONUS, (state, action) => action.payload.bonusId)
        .addMatcher(isAllbonusesLoaded, () => unselected)
        .addCase(CLEAR_BONUS, () => unselected);
});

export default bonusIdReducer;
