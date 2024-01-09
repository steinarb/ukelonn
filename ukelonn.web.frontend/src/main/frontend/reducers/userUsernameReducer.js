import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_USERNAME,
    SELECT_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';

const defaultValue = '';

const userUsernameReducer = createReducer('', builder => {
    builder
        .addCase(MODIFY_USER_USERNAME, (state, action) => action.payload)
        .addCase(SELECT_USER, (state, action) => action.payload.username)
        .addCase(CLEAR_USER, () => defaultValue)
        .addCase(CLEAR_USER_AND_PASSWORDS, () => defaultValue);
});

export default userUsernameReducer;
