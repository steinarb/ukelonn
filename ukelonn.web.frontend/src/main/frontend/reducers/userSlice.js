import { createSlice } from '@reduxjs/toolkit';
import { api } from '../api';
import { isUsersLoaded } from '../matchers';

const initialState = {
    userid: -1,
    username: '',
    email: '',
    firstname: '',
    lastname: '',
};

export const userSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {
        selectUser: (_, action) => action.payload,
        clearUser: () => initialState,
        setUsername: (state, action) => ({ ...state, username: action.payload }),
        setEmail: (state, action) => ({ ...state, email: action.payload }),
        setFirstname: (state, action) => ({ ...state, firstname: action.payload }),
        setLastname: (state, action) => ({ ...state, lastname: action.payload }),
    },
    extraReducers: builder => {
        builder
            .addMatcher(isUsersLoaded, (state, action) => initialState)
    },
});

export const { selectUser, clearUser, setUsername, setEmail, setFirstname, setLastname } = userSlice.actions;
export default userSlice.reducer;
