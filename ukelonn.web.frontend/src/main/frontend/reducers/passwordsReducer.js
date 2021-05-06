import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_PASSWORDS,
    MODIFY_USER_RECEIVE,
    CREATE_USER_RECEIVE,
    MODIFY_USER_PASSWORD_RECEIVE,
    CLEAR_USER_AND_PASSWORD,
} from '../actiontypes';
import { emptyPasswords } from './constants';

const passwordsReducer = createReducer({ ...emptyPasswords }, {
    [UPDATE_PASSWORDS]: (state, action) => {
        const updatedState = { ...state, ...action.payload };
        const passwordsNotIdentical = checkIfPasswordsAreNotIdentical(updatedState);
        return { ...updatedState, passwordsNotIdentical };
    },
    [MODIFY_USER_RECEIVE]: () => ({ ...emptyPasswords }),
    [CREATE_USER_RECEIVE]: () => ({ ...emptyPasswords }),
    [MODIFY_USER_PASSWORD_RECEIVE]: () => ({ ...emptyPasswords }),
    [CLEAR_USER_AND_PASSWORD]: () => ({ ...emptyPasswords }),
});

export default passwordsReducer;

function checkIfPasswordsAreNotIdentical(passwords) {
    let { password1, password2 } = passwords;
    if (!password2) {
        return false; // if second password is empty we don't compare because it probably hasn't been typed into yet
    }

    return password1 !== password2;
}
