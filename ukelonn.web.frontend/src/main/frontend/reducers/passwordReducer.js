import { createReducer } from 'redux-starter-kit';
import {
    UPDATE,
} from '../actiontypes';

const passwordReducer = createReducer(null, {
    [UPDATE]: (state, action) => {
        if (!(action.payload && action.payload.password)) {
            return state;
        }
        return action.payload.password;
    },
});

export default passwordReducer;
