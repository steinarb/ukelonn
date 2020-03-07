import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_ACCOUNT,
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
} from '../actiontypes';

const accountReducer = createReducer({ firstName: 'Ukjent', fullName: '', balance: 0.0 }, {
    [UPDATE_ACCOUNT]: (state, action) => ({ ...action.payload }),
    [ACCOUNT_RECEIVE]: (state, action) => action.payload,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload,
});

export default accountReducer;
