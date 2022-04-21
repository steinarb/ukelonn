import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_ICONURL,
    SELECTED_BONUS,
    CLEAR_BONUS,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = '';

const bonusIconurlReducer = createReducer(defaultValue, {
    [MODIFY_BONUS_ICONURL]: (state, action) => action.payload,
    [SELECTED_BONUS]: (state, action) => isUnselected(action.payload.bonusId) ? defaultValue : action.payload.iconurl,
    [CLEAR_BONUS]: () => defaultValue,
});

export default bonusIconurlReducer;
