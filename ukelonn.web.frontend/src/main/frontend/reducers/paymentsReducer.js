import { createReducer } from '@reduxjs/toolkit';
import {
    RECENTPAYMENTS_RECEIVE,
} from '../actiontypes';
import { emptyPerformedTransaction } from './constants';

const paymenttypeReducer = createReducer([], {
    [RECENTPAYMENTS_RECEIVE]: (state, action) => action.payload,
});

export default paymenttypeReducer;
