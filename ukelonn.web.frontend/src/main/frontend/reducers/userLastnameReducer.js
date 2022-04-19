import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_LASTNAME,
    CLEAR_USER,
} from '../actiontypes';

const userLastnameReducer = createReducer('', {
    [MODIFY_USER_LASTNAME]: (state, action) => action.payload,
    [CLEAR_USER]: () => '',
});

export default userLastnameReducer;
