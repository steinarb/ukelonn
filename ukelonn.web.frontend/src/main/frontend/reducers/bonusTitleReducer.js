import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_TITLE,
    SELECTED_BONUS,
    CLEAR_BONUS,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = '';

const bonusTitleReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(MODIFY_BONUS_TITLE, (state, action) => action.payload)
        .addCase(SELECTED_BONUS, (state, action) => isUnselected(action.payload.bonusId) ? defaultValue : action.payload.title)
        .addCase(CLEAR_BONUS, () => defaultValue);
});

export default bonusTitleReducer;
