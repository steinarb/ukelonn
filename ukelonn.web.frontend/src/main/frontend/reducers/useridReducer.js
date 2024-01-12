import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';
const unselected = -1;

const userIdReducer = createReducer(unselected, builder => {
    builder
        .addCase(SELECT_USER, (state, action) => action.payload.userid)
        .addCase(CLEAR_USER, () => unselected)
        .addCase(CLEAR_USER_AND_PASSWORDS, () => unselected);
});

export default userIdReducer;
