import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_LASTNAME,
    SELECT_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';

const defaultValue = '';

const userLastnameReducer = createReducer('', builder => {
    builder
        .addCase(MODIFY_USER_LASTNAME, (state, action) => action.payload)
        .addCase(SELECT_USER, (state, action) => action.payload.lastname)
        .addCase(CLEAR_USER, () => defaultValue)
        .addCase(CLEAR_USER_AND_PASSWORDS, () => defaultValue);
});

export default userLastnameReducer;
