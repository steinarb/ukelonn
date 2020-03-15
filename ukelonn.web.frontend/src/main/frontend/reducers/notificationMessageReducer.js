import { createReducer } from '@reduxjs/toolkit';
import {
    RECEIVED_NOTIFICATION,
    UPDATE_NOTIFICATIONMESSAGE,
} from '../actiontypes';

const notificationMessageReducer = createReducer(false, {
    [RECEIVED_NOTIFICATION]: (state, action) => action.payload[0],
    [UPDATE_NOTIFICATIONMESSAGE]: (state, action) => action.payload,
});

export default notificationMessageReducer;
