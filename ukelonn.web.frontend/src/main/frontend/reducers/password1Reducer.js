import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_PASSWORD1,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';
const defaultValue = '';

const password1Reducer = createReducer(defaultValue, builder => {
    builder
        .addCase(MODIFY_PASSWORD1, (state, action) => action.payload)
        .addCase(CLEAR_USER_AND_PASSWORDS, () => defaultValue);
});

export default password1Reducer;
