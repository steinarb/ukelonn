import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE,
    JOBTYPELIST_RECEIVE,
    MODIFY_JOBTYPE_RECEIVE,
    CREATE_JOBTYPE_RECEIVE,
    PAYMENTTYPELIST_RECEIVE,
    MODIFY_PAYMENTTYPE_RECEIVE,
    CREATE_PAYMENTTYPE_RECEIVE,
} from '../actiontypes';
import { emptyTransactionType } from './constants';

const transactiontypeReducer = createReducer({ ...emptyTransactionType }, {
    [UPDATE]: (state, action) => {
        if (!action.payload) { return state; }
        const transactiontype = action.payload.transactiontype;
        if (transactiontype === undefined) { return state; }
        return { ...state, ...transactiontype };
    },
    [JOBTYPELIST_RECEIVE]: (state, action) => ({ ...emptyTransactionType }),
    [MODIFY_JOBTYPE_RECEIVE]: (state, action) => ({ ...emptyTransactionType }),
    [CREATE_JOBTYPE_RECEIVE]: (state, action) => ({ ...emptyTransactionType }),
    [PAYMENTTYPELIST_RECEIVE]: (state, action) => ({ ...emptyTransactionType }),
    [MODIFY_PAYMENTTYPE_RECEIVE]: (state, action) => ({ ...emptyTransactionType }),
    [CREATE_PAYMENTTYPE_RECEIVE]: (state, action) => ({ ...emptyTransactionType }),
});

export default transactiontypeReducer;
