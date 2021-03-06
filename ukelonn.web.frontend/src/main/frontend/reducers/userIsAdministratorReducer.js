import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_USER_IS_ADMINISTRATOR,
    RECEIVE_ADMIN_STATUS,
    CHANGE_ADMIN_STATUS_RESPONSE,
} from '../actiontypes';

export default createReducer(false, {
    [UPDATE_USER_IS_ADMINISTRATOR]: (state, action) => action.payload,
    [RECEIVE_ADMIN_STATUS]: (state, action) => action.payload.administrator,
    [CHANGE_ADMIN_STATUS_RESPONSE]: (state, action) => false,
});
