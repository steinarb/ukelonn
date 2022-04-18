import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_EMAIL,
    CLEAR_USER,
} from '../actiontypes';

const userEmailReducer = createReducer('', {
    [MODIFY_USER_EMAIL]: (state, action) => action.payload,
    [CLEAR_USER]: () => '',
});

export default userEmailReducer;
