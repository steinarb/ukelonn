import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_IS_ADMINISTRATOR,
    RECEIVE_ADMIN_STATUS,
    SELECTED_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = false;

export default createReducer(defaultValue, {
    [MODIFY_USER_IS_ADMINISTRATOR]: (state, action) => action.payload,
    [RECEIVE_ADMIN_STATUS]: (state, action) => action.payload.administrator,
    [SELECTED_USER]: (state, action) => isUnselected(action.payload.userid) ? defaultValue : state,
    [CLEAR_USER]: () => defaultValue,
    [CLEAR_USER_AND_PASSWORDS]: () => defaultValue,
});
