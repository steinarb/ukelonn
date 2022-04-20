import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_USERNAME,
    SELECTED_USER,
    CLEAR_USER,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = '';

const userUsernameReducer = createReducer('', {
    [MODIFY_USER_USERNAME]: (state, action) => action.payload,
    [SELECTED_USER]: (state, action) => isUnselected(action.payload.userid) ? defaultValue : action.payload.username,
    [CLEAR_USER]: () => defaultValue,
});

export default userUsernameReducer;
