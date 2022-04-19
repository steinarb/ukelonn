import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_USERNAME,
    CLEAR_USER,
} from '../actiontypes';

const userUsernameReducer = createReducer('', {
    [MODIFY_USER_USERNAME]: (state, action) => action.payload,
    [CLEAR_USER]: () => '',
});

export default userUsernameReducer;
