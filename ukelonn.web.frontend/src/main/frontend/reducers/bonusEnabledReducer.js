import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_ENABLED,
    CLEAR_BONUS,
} from '../actiontypes';
const defaultValue = false;

const bonusEnabledReducer = createReducer(defaultValue, {
    [MODIFY_BONUS_ENABLED]: (state, action) => action.payload,
    [CLEAR_BONUS]: () => defaultValue,
});

export default bonusEnabledReducer;
