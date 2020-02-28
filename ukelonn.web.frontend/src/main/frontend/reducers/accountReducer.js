import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE,
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
} from '../actiontypes';

const accountReducer = createReducer({ firstName: 'Ukjent', fullName: '', balance: 0.0 }, {
    [UPDATE]: (state, action) => {
        if (!action.payload) { return state; }
        const account = action.payload.account;
        if (account === undefined) { return state; }
        return { ...state, ...account };
    },
    [ACCOUNT_RECEIVE]: (state, action) => action.payload,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload,
});

export default accountReducer;
