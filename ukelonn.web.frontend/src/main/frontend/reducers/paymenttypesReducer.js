import { createReducer } from '@reduxjs/toolkit';
import {
    PAYMENTTYPES_RECEIVE,
    MODIFY_PAYMENTTYPE_RECEIVE,
    CREATE_PAYMENTTYPE_RECEIVE,
} from '../actiontypes';

const paymenttypesReducer = createReducer([], builder => {
    builder
        .addCase(PAYMENTTYPES_RECEIVE, (state, action) => action.payload)
        .addCase(MODIFY_PAYMENTTYPE_RECEIVE, (state, action) => action.payload)
        .addCase(CREATE_PAYMENTTYPE_RECEIVE, (state, action) => action.payload);
});

export default paymenttypesReducer;
