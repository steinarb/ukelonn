import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_DESCRIPTION,
    SELECTED_BONUS,
    CLEAR_BONUS,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = '';

const bonusDescriptionReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(MODIFY_BONUS_DESCRIPTION, (state, action) => action.payload)
        .addCase(SELECTED_BONUS, (state, action) => isUnselected(action.payload.bonusId) ? defaultValue : action.payload.description)
        .addCase(CLEAR_BONUS, () => defaultValue);
});

export default bonusDescriptionReducer;
