import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_EMAIL,
    SELECT_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';

const defaultValue = '';

const userEmailReducer = createReducer('', {
    [MODIFY_USER_EMAIL]: (state, action) => action.payload,
    [SELECT_USER]: (state, action) => action.payload.email,
    [CLEAR_USER]: () => defaultValue,
    [CLEAR_USER_AND_PASSWORDS]: () => defaultValue,
});

export default userEmailReducer;
