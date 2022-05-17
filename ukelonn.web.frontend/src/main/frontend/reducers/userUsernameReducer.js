import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_USERNAME,
    SELECT_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';

const defaultValue = '';

const userUsernameReducer = createReducer('', {
    [MODIFY_USER_USERNAME]: (state, action) => action.payload,
    [SELECT_USER]: (state, action) => action.payload.username,
    [CLEAR_USER]: () => defaultValue,
    [CLEAR_USER_AND_PASSWORDS]: () => defaultValue,
});

export default userUsernameReducer;
