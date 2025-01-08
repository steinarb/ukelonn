import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_FACTOR,
    SELECTED_BONUS,
    CLEAR_BONUS,
} from '../actiontypes';
import { isAllbonusesLoaded } from '../matchers';
import { isUnselected } from '../common/reducers';

const defaultValue = 1;

const bonusFactorReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(MODIFY_BONUS_FACTOR, (state, action) => action.payload)
        .addCase(SELECTED_BONUS, (state, action) => isUnselected(action.payload.bonusId) ? defaultValue : action.payload.bonusFactor)
        .addMatcher(isAllbonusesLoaded, () => defaultValue)
        .addCase(CLEAR_BONUS, () => defaultValue);
});

export default bonusFactorReducer;
