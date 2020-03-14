import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_USERNAME,
} from '../actiontypes';

const usernameReducer = createReducer(null, {
    [UPDATE_USERNAME]: (state, action) => action.payload,
});

export default usernameReducer;
