import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_ENABLED,
    SELECTED_BONUS,
    CLEAR_BONUS,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = false;

const bonusEnabledReducer = createReducer(defaultValue, {
    [MODIFY_BONUS_ENABLED]: (state, action) => action.payload,
    [SELECTED_BONUS]: (state, action) => isUnselected(action.payload.bonusId) ? defaultValue : action.payload.enabled,
    [CLEAR_BONUS]: () => defaultValue,
});

export default bonusEnabledReducer;
