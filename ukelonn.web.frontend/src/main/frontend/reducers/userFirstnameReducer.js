import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_FIRSTNAME,
    SELECT_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';

const defaultValue = '';

const userFirstnameReducer = createReducer('', builder => {
    builder
        .addCase(MODIFY_USER_FIRSTNAME, (state, action) => action.payload)
        .addCase(SELECT_USER, (state, action) => action.payload.firstname)
        .addCase(CLEAR_USER, () => defaultValue)
        .addCase(CLEAR_USER_AND_PASSWORDS, () => defaultValue);
});

export default userFirstnameReducer;
