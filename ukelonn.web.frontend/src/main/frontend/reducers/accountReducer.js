import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_ACCOUNT,
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    INITIAL_LOGIN_STATE_RECEIVE,
    LOGIN_RECEIVE,
} from '../actiontypes';
import { emptyAccount } from '../constants';
import { isAdmin } from '../common/roles';

const accountReducer = createReducer(emptyAccount, {
    [UPDATE_ACCOUNT]: (state, action) => ({ ...action.payload }),
    [ACCOUNT_RECEIVE]: (state, action) => action.payload,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload,
    [INITIAL_LOGIN_STATE_RECEIVE]: (state, action) => isAdmin(action) ? { ...emptyAccount } : state,
    [LOGIN_RECEIVE]: (state, action) => isAdmin(action) ? { ...emptyAccount } : state,
});

export default accountReducer;
