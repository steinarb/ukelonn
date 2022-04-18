import { createReducer } from '@reduxjs/toolkit';
import {
    RECENTPAYMENTS_RECEIVE,
    CLEAR_ACCOUNT,
} from '../actiontypes';
const defaultValue = [];

const paymenttypeReducer = createReducer(defaultValue, {
    [RECENTPAYMENTS_RECEIVE]: (state, action) => action.payload,
    [CLEAR_ACCOUNT]: () => defaultValue,
});

export default paymenttypeReducer;
