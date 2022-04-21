import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_LASTNAME,
    SELECTED_USER,
    CLEAR_USER,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = '';

const userLastnameReducer = createReducer('', {
    [MODIFY_USER_LASTNAME]: (state, action) => action.payload,
    [SELECTED_USER]: (state, action) => isUnselected(action.payload.userid) ? defaultValue : action.payload.lastname,
    [CLEAR_USER]: () => defaultValue,
});

export default userLastnameReducer;
