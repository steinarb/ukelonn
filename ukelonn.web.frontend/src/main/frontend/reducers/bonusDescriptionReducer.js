import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_DESCRIPTION,
    CLEAR_BONUS,
} from '../actiontypes';
const defaultValue = '';

const bonusDescriptionReducer = createReducer(defaultValue, {
    [MODIFY_BONUS_DESCRIPTION]: (state, action) => action.payload,
    [CLEAR_BONUS]: () => defaultValue,
});

export default bonusDescriptionReducer;
