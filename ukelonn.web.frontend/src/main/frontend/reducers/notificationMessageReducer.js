import { createReducer } from 'redux-starter-kit';
import {
    RECEIVED_NOTIFICATION,
} from '../actiontypes';

const notificationMessageReducer = createReducer(false, {
    [RECEIVED_NOTIFICATION]: (state, action) => action.payload[0],
});

export default notificationMessageReducer;
