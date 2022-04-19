import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_ICONURL,
    CLEAR_BONUS,
} from '../actiontypes';
const defaultValue = '';

const bonusIconurlReducer = createReducer(defaultValue, {
    [MODIFY_BONUS_ICONURL]: (state, action) => action.payload,
    [CLEAR_BONUS]: () => defaultValue,
});

export default bonusIconurlReducer;
