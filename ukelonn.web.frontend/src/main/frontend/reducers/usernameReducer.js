import { createReducer } from 'redux-starter-kit';
import {
    UPDATE,
} from '../actiontypes';

const usernameReducer = createReducer(null, {
    [UPDATE]: (state, action) => {
        if (!(action.payload && action.payload.username)) {
            return state;
        }
        return action.payload.username;
    },
});

export default usernameReducer;
