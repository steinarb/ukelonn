import { createReducer } from '@reduxjs/toolkit';
import {
    RECEIVE_ALL_BONUSES,
    CREATE_BONUS_RECEIVE,
    MODIFY_BONUS_RECEIVE,
    DELETE_BONUS_RECEIVE,
} from '../actiontypes';

const allbonusesReducer = createReducer([], builder => {
    builder
        .addCase(RECEIVE_ALL_BONUSES, (state, action) => action.payload)
        .addCase(DELETE_BONUS_RECEIVE, (state, action) => action.payload)
        .addCase(MODIFY_BONUS_RECEIVE, (state, action) => action.payload)
        .addCase(CREATE_BONUS_RECEIVE, (state, action) => action.payload);
});

export default allbonusesReducer;
