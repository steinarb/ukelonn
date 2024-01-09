import { createReducer } from '@reduxjs/toolkit';
import {
    RECEIVED_NOTIFICATION,
    UPDATE_NOTIFICATIONMESSAGE,
} from '../actiontypes';

const notificationMessageReducer = createReducer(false, builder => {
    builder
        .addCase(RECEIVED_NOTIFICATION, (state, action) => action.payload[0])
        .addCase(UPDATE_NOTIFICATIONMESSAGE, (state, action) => action.payload);
});

export default notificationMessageReducer;
