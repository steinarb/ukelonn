import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE,
    MODIFY_USER_RECEIVE,
    CREATE_USER_RECEIVE,
    MODIFY_USER_PASSWORD_RECEIVE,
    CLEAR_USER_AND_PASSWORD,
} from '../actiontypes';
import { emptyPasswords } from './constants';

const passwordsReducer = createReducer({ ...emptyPasswords }, {
    [UPDATE]: (state, action) => {
        if (!action.payload) { return state; }
        const passwords = action.payload.passwords;
        if (passwords === undefined) { return state; }
        return { ...state, ...passwords };
    },
    [MODIFY_USER_RECEIVE]: (state, action) => ({ ...emptyPasswords }),
    [CREATE_USER_RECEIVE]: (state, action) => ({ ...emptyPasswords }),
    [MODIFY_USER_PASSWORD_RECEIVE]: (state, action) => ({ ...emptyPasswords }),
    [CLEAR_USER_AND_PASSWORD]: (state, action) => ({ ...emptyPasswords }),
});

export default passwordsReducer;
