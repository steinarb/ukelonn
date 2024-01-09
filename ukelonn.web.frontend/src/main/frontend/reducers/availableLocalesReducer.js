import { createReducer } from '@reduxjs/toolkit';
import {
    AVAILABLE_LOCALES_RECEIVE,
} from '../actiontypes';

const availableLocalesReducer = createReducer([], builder => {
    builder
        .addCase(AVAILABLE_LOCALES_RECEIVE, (state, action) => action.payload);
});

export default availableLocalesReducer;
