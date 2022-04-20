import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_FACTOR,
    SELECTED_BONUS,
    CLEAR_BONUS,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = 1;

const bonusFactorReducer = createReducer(defaultValue, {
    [MODIFY_BONUS_FACTOR]: (state, action) => action.payload,
    [SELECTED_BONUS]: (state, action) => isUnselected(action.payload.bonusId) ? defaultValue : action.payload.bonusFactor,
    [CLEAR_BONUS]: () => defaultValue,
});

export default bonusFactorReducer;
