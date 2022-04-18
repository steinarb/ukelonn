import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';
const unselected = -1;

const userIdReducer = createReducer(unselected, {
    [SELECT_USER]: (state, action) => action.payload,
    [CLEAR_USER]: () => unselected,
    [CLEAR_USER_AND_PASSWORDS]: () => unselected,
});

export default userIdReducer;
