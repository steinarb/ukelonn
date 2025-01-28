import { createReducer } from '@reduxjs/toolkit';
import { selectUser, clearUser } from './userSlice';
import {
    MODIFY_USER_IS_ADMINISTRATOR,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';
import { api } from '../api';
import { isUnselected } from '../common/reducers';

const defaultValue = false;

export default createReducer(defaultValue, builder => {
    builder
        .addCase(MODIFY_USER_IS_ADMINISTRATOR, (state, action) => action.payload)
        .addMatcher(api.endpoints.postUserAdminstatus.matchFulfilled, (state, action) => action.payload.administrator)
        .addCase(selectUser, (state, action) => isUnselected(action.payload.userid) ? defaultValue : state)
        .addCase(clearUser, () => defaultValue)
        .addCase(CLEAR_USER_AND_PASSWORDS, () => defaultValue);
});
