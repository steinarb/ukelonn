import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_PASSWORDS_NOT_IDENTICAL,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';
const defaultValue = false;

const passwordsNotIdenticalReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(MODIFY_PASSWORDS_NOT_IDENTICAL, (state, action) => action.payload)
        .addCase(CLEAR_USER_AND_PASSWORDS, () => defaultValue);
});

export default passwordsNotIdenticalReducer;
