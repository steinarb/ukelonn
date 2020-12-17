import { createReducer } from '@reduxjs/toolkit';
import {
    DEFAULT_LOCALE_RECEIVE,
} from '../actiontypes';

const localeReducer = createReducer('', {
    [DEFAULT_LOCALE_RECEIVE]: (state, action) => action.payload,
});

export default localeReducer;
