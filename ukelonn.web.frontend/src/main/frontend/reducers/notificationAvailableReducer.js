import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_NOTIFICATIONAVAILABLE,
} from '../actiontypes';

const notificationAvailableReducer = createReducer(false, builder => {
    builder
        .addCase(UPDATE_NOTIFICATIONAVAILABLE, (state, action) => action.payload);
});

export default notificationAvailableReducer;
