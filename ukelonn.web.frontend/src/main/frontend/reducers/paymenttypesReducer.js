import { createReducer } from '@reduxjs/toolkit';
import {
    PAYMENTTYPELIST_RECEIVE,
    PAYMENTTYPES_RECEIVE,
    MODIFY_PAYMENTTYPE_RECEIVE,
    CREATE_PAYMENTTYPE_RECEIVE,
} from '../actiontypes';

const paymenttypesReducer = createReducer([], {
    [PAYMENTTYPELIST_RECEIVE]: (state, action) => action.payload,
    [PAYMENTTYPES_RECEIVE]: (state, action) => action.payload,
    [MODIFY_PAYMENTTYPE_RECEIVE]: (state, action) => action.payload,
    [CREATE_PAYMENTTYPE_RECEIVE]: (state, action) => action.paymenttypes,
});

export default paymenttypesReducer;
