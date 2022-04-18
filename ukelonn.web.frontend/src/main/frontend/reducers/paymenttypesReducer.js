import { createReducer } from '@reduxjs/toolkit';
import {
    PAYMENTTYPES_RECEIVE,
    MODIFY_PAYMENTTYPE_RECEIVE,
    CREATE_PAYMENTTYPE_RECEIVE,
} from '../actiontypes';

const paymenttypesReducer = createReducer([], {
    [PAYMENTTYPES_RECEIVE]: (state, action) => action.payload,
    [MODIFY_PAYMENTTYPE_RECEIVE]: (state, action) => action.payload,
    [CREATE_PAYMENTTYPE_RECEIVE]: (state, action) => action.payload,
});

export default paymenttypesReducer;
