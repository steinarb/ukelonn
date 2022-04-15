import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_TITLE,
    CLEAR_BONUS,
} from '../actiontypes';
const defaultValue = '';

const bonusTitleReducer = createReducer(defaultValue, {
    [MODIFY_BONUS_TITLE]: (state, action) => action.payload,
    [CLEAR_BONUS]: () => defaultValue,
});

export default bonusTitleReducer;
