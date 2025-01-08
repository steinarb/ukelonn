import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_USER_IS_ADMINISTRATOR,
    SELECT_USER,
    CLEAR_USER,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';
import { api } from '../api';
import { isUnselected } from '../common/reducers';

const defaultValue = false;

export default createReducer(defaultValue, builder => {
    builder
        .addCase(MODIFY_USER_IS_ADMINISTRATOR, (state, action) => action.payload)
        .addMatcher(api.endpoints.postUserAdminstatus.matchFulfilled, (state, action) => action.payload.administrator)
        .addCase(SELECT_USER, (state, action) => isUnselected(action.payload.userid) ? defaultValue : state)
        .addCase(CLEAR_USER, () => defaultValue)
        .addCase(CLEAR_USER_AND_PASSWORDS, () => defaultValue);
});
