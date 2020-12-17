import { createReducer } from '@reduxjs/toolkit';
import {
    DISPLAY_TEXTS_RECEIVE,
} from '../actiontypes';

const displayTextsReducer = createReducer([], {
    [DISPLAY_TEXTS_RECEIVE]: (state, action) => action.payload,
});

export default displayTextsReducer;
