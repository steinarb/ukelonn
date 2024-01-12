import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_ICONURL,
    SELECTED_BONUS,
    CLEAR_BONUS,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = '';

const bonusIconurlReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(MODIFY_BONUS_ICONURL, (state, action) => action.payload)
        .addCase(SELECTED_BONUS, (state, action) => isUnselected(action.payload.bonusId) ? defaultValue : action.payload.iconurl)
        .addCase(CLEAR_BONUS, () => defaultValue);
});

export default bonusIconurlReducer;
