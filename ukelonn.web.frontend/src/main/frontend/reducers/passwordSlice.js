import { createSlice } from '@reduxjs/toolkit';
import { api } from '../api';
import { isUsersLoaded } from '../matchers';

const initialState = {
    password1: '',
    password2: '',
    passwordsNotIdentical: false,
};

export const passwordSlice = createSlice({
    name: 'password',
    initialState,
    reducers: {
        clearPassword: () => initialState,
        setPassword1: (state, action) => ({ ...state, password1: action.payload, passwordsNotIdentical: compare(action.payload, state.password2) }),
        setPassword2: (state, action) => ({ ...state, password2: action.payload, passwordsNotIdentical: compare(state.password1, action.payload) }),
    },
    extraReducers: builder => {
        builder
            .addMatcher(isUsersLoaded, (state, action) => initialState)
    },
});

export const { clearPassword, setPassword1, setPassword2 } = passwordSlice.actions;
export default passwordSlice.reducer;

function compare(password1, password2) {
    if (!password2) {
        // if second password is empty we don't compare because it probably hasn't been typed into yet
        return false;
    } else {
        return password1 !== password2;
    }
}
