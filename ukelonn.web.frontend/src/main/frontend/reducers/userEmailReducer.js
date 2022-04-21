import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_EMAIL,
    SELECTED_USER,
    CLEAR_USER,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = '';

const userEmailReducer = createReducer('', {
    [MODIFY_USER_EMAIL]: (state, action) => action.payload,
    [SELECTED_USER]: (state, action) => isUnselected(action.payload.userid) ? defaultValue : action.payload.email,
    [CLEAR_USER]: () => defaultValue,
});

export default userEmailReducer;
