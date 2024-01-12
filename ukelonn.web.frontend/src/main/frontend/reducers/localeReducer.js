import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_LOCALE,
} from '../actiontypes';

const localeReducer = createReducer('', builder => {
    builder
        .addCase(UPDATE_LOCALE, (state, action) => action.payload);
});

export default localeReducer;
