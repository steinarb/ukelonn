import { createReducer } from '@reduxjs/toolkit';
import {
    ACCOUNT_RECEIVE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    SELECT_ACCOUNT,
    MODIFY_ACCOUNT_ID,
    CLEAR_ACCOUNT,
} from '../actiontypes';
const unselectedAccount = -1;

const accountIdReducer = createReducer(unselectedAccount, {
    [ACCOUNT_RECEIVE]: (state, action) => action.payload.accountId,
    [REGISTERJOB_RECEIVE]: (state, action) => action.payload.accountId,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.accountId,
    [SELECT_ACCOUNT]: (state, action) => parseInt(action.payload),
    [MODIFY_ACCOUNT_ID]: (state, action) => parseInt(action.payload),
    [CLEAR_ACCOUNT]: () => unselectedAccount,
});

export default accountIdReducer;
