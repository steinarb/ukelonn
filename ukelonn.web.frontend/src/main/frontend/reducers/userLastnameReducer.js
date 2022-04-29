import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_LASTNAME,
    SELECTED_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = '';

const userLastnameReducer = createReducer('', {
    [MODIFY_USER_LASTNAME]: (state, action) => action.payload,
    [SELECTED_USER]: (state, action) => isUnselected(action.payload.userid) ? defaultValue : action.payload.lastname,
    [CLEAR_USER]: () => defaultValue,
    [CLEAR_USER_AND_PASSWORDS]: () => defaultValue,
});

export default userLastnameReducer;
