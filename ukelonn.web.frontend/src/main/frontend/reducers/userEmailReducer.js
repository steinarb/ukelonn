import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_EMAIL,
    SELECTED_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = '';

const userEmailReducer = createReducer('', {
    [MODIFY_USER_EMAIL]: (state, action) => action.payload,
    [SELECTED_USER]: (state, action) => isUnselected(action.payload.userid) ? defaultValue : action.payload.email,
    [CLEAR_USER]: () => defaultValue,
    [CLEAR_USER_AND_PASSWORDS]: () => defaultValue,
});

export default userEmailReducer;
