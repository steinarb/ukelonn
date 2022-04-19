import { createReducer } from '@reduxjs/toolkit';
import {
    RECEIVE_ALL_BONUSES,
    CREATE_BONUS_RECEIVE,
    MODIFY_BONUS_RECEIVE,
    DELETE_BONUS_RECEIVE,
} from '../actiontypes';

const allbonusesReducer = createReducer([], {
    [RECEIVE_ALL_BONUSES]: (state, action) => action.payload,
    [DELETE_BONUS_RECEIVE]: (state, action) => action.payload,
    [MODIFY_BONUS_RECEIVE]: (state, action) => action.payload,
    [CREATE_BONUS_RECEIVE]: (state, action) => action.payload,
});

export default allbonusesReducer;
