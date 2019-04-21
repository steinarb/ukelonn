import { createReducer } from 'redux-starter-kit';
import {
    UPDATE,
} from '../actiontypes';

const notificationAvailableReducer = createReducer(false, {
    [UPDATE]: (state, action) => {
        if (!action.payload) { return state; }
        const notificationAvailable = action.payload.notificationAvailable;
        if (notificationAvailable === undefined) { return state; }
        return notificationAvailable;
    },
});

export default notificationAvailableReducer;
