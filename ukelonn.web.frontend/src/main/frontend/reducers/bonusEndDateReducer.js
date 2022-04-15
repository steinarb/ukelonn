import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_END_DATE,
    CLEAR_BONUS,
} from '../actiontypes';

const bonusEndDateReducer = createReducer(new Date().toISOString(), {
    [MODIFY_BONUS_END_DATE]: (state, action) => action.payload,
    [CLEAR_BONUS]: () => new Date().toISOString(),
});

export default bonusEndDateReducer;
