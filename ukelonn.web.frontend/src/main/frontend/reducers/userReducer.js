import { createReducer } from 'redux-starter-kit';
import {
    UPDATE,
    MODIFY_USER_RECEIVE,
    CREATE_USER_RECEIVE,
    MODIFY_USER_PASSWORD_RECEIVE,
    CLEAR_USER_AND_PASSWORD,
} from '../actiontypes';
import { emptyUser } from './constants';

const userReducer = createReducer({ ...emptyUser }, {
    [UPDATE]: (state, action) => {
        if (!action.payload) { return state; }
        const user = action.payload.user;
        if (user === undefined) { return state; }
        return { ...state, ...user };
    },
    [MODIFY_USER_RECEIVE]: (state, action) => ({ ...emptyUser }),
    [CREATE_USER_RECEIVE]: (state, action) => ({ ...emptyUser }),
    [MODIFY_USER_PASSWORD_RECEIVE]: (state, action) => ({ ...emptyUser }),
    [CLEAR_USER_AND_PASSWORD]: (state, action) => ({ ...emptyUser }),
});

export default userReducer;
