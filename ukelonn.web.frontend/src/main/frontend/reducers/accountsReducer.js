import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNTS_RECEIVE,
} from '../actiontypes';

const accountsReducer = createReducer([], {
    [ACCOUNTS_RECEIVE]: (state, action) => action.payload,
});

export default accountsReducer;
