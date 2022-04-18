import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_START_DATE,
    CLEAR_BONUS,
} from '../actiontypes';

const bonusStartDateReducer = createReducer(new Date().toISOString(), {
    [MODIFY_BONUS_START_DATE]: (state, action) => action.payload,
    [CLEAR_BONUS]: () => new Date().toISOString(),
});

export default bonusStartDateReducer;
