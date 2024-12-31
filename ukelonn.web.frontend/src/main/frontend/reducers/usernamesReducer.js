import { createReducer } from '@reduxjs/toolkit';
import { isUsersLoaded } from '../matchers';

const usernamesReducer = createReducer([], builder => {
    builder
        .addMatcher(isUsersLoaded, (state, action) => action.payload.map(u => u.username));
});

export default usernamesReducer;
