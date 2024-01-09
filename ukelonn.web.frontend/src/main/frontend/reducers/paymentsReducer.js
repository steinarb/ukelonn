import { createReducer } from '@reduxjs/toolkit';
import {
    RECENTPAYMENTS_RECEIVE,
} from '../actiontypes';
const defaultValue = [];

const paymenttypeReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(RECENTPAYMENTS_RECEIVE, (state, action) => action.payload);
});

export default paymenttypeReducer;
