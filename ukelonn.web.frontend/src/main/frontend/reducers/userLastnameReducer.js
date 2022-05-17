import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_LASTNAME,
    SELECT_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';

const defaultValue = '';

const userLastnameReducer = createReducer('', {
    [MODIFY_USER_LASTNAME]: (state, action) => action.payload,
    [SELECT_USER]: (state, action) => action.payload.lastname,
    [CLEAR_USER]: () => defaultValue,
    [CLEAR_USER_AND_PASSWORDS]: () => defaultValue,
});

export default userLastnameReducer;
