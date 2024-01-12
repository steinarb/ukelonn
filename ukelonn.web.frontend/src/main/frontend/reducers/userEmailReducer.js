import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_EMAIL,
    SELECT_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';

const defaultValue = '';

const userEmailReducer = createReducer('', builder => {
    builder
        .addCase(MODIFY_USER_EMAIL, (state, action) => action.payload)
        .addCase(SELECT_USER, (state, action) => action.payload.email)
        .addCase(CLEAR_USER, () => defaultValue)
        .addCase(CLEAR_USER_AND_PASSWORDS, () => defaultValue);
});

export default userEmailReducer;
