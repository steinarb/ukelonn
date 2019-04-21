import { createReducer } from 'redux-starter-kit';
import {
    UPDATE,
} from '../actiontypes';

const passwordsNotIdenticalReducer = createReducer(false, {
    [UPDATE]: (state, action) => {
        if (!action.payload) { return state; }
        const passwordsNotIdentical = action.payload.passwordsNotIdentical;
        if (passwordsNotIdentical === undefined) { return state; }
        return passwordsNotIdentical;
    },
});

export default passwordsNotIdenticalReducer;
