import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_FIRSTNAME,
    CLEAR_USER,
} from '../actiontypes';

const userFirstnameReducer = createReducer('', {
    [MODIFY_USER_FIRSTNAME]: (state, action) => action.payload,
    [CLEAR_USER]: () => '',
});

export default userFirstnameReducer;
