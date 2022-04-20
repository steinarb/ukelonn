import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_END_DATE,
    SELECTED_BONUS,
    CLEAR_BONUS,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const bonusEndDateReducer = createReducer(new Date().toISOString(), {
    [MODIFY_BONUS_END_DATE]: (state, action) => action.payload,
    [SELECTED_BONUS]: (state, action) => isUnselected(action.payload.bonusId) ? new Date().toISOString() : action.payload.endDate,
    [CLEAR_BONUS]: () => new Date().toISOString(),
});

export default bonusEndDateReducer;
