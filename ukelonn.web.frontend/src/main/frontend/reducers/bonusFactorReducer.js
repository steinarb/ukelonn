import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_FACTOR,
    CLEAR_BONUS,
} from '../actiontypes';
const defaultValue = 1;

const bonusFactorReducer = createReducer(defaultValue, {
    [MODIFY_BONUS_FACTOR]: (state, action) => action.payload,
    [CLEAR_BONUS]: () => defaultValue,
});

export default bonusFactorReducer;
