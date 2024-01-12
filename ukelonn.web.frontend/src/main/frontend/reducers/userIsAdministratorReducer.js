import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_IS_ADMINISTRATOR,
    RECEIVE_ADMIN_STATUS,
    SELECT_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const defaultValue = false;

export default createReducer(defaultValue, builder => {
    builder
        .addCase(MODIFY_USER_IS_ADMINISTRATOR, (state, action) => action.payload)
        .addCase(RECEIVE_ADMIN_STATUS, (state, action) => action.payload.administrator)
        .addCase(SELECT_USER, (state, action) => isUnselected(action.payload.userid) ? defaultValue : state)
        .addCase(CLEAR_USER, () => defaultValue)
        .addCase(CLEAR_USER_AND_PASSWORDS, () => defaultValue);
});
