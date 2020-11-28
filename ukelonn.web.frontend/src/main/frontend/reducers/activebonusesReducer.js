import { createReducer } from '@reduxjs/toolkit';
import {
    RECEIVE_ACTIVE_BONUSES,
} from '../actiontypes';

const activebonusesReducer = createReducer([], {
    [RECEIVE_ACTIVE_BONUSES]: (state, action) => action.payload,
});

export default activebonusesReducer;
