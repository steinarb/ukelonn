import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_FIRSTNAME,
    SELECT_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';

const defaultValue = '';

const userFirstnameReducer = createReducer('', {
    [MODIFY_USER_FIRSTNAME]: (state, action) => action.payload,
    [SELECT_USER]: (state, action) => action.payload.firstname,
    [CLEAR_USER]: () => defaultValue,
    [CLEAR_USER_AND_PASSWORDS]: () => defaultValue,
});

export default userFirstnameReducer;
