import { createReducer } from '@reduxjs/toolkit';
import {
    UPDATE_TRANSACTIONTYPE,
    JOBTYPELIST_RECEIVE,
    MODIFY_JOBTYPE_RECEIVE,
    CREATE_JOBTYPE_RECEIVE,
    PAYMENTTYPES_RECEIVE,
    MODIFY_PAYMENTTYPE_RECEIVE,
    CREATE_PAYMENTTYPE_RECEIVE,
} from '../actiontypes';
import { emptyTransactionType } from './constants';

const transactiontypeReducer = createReducer({ ...emptyTransactionType }, {
    [UPDATE_TRANSACTIONTYPE]: (state, action) => ({ ...state, ...action.payload }),
    [JOBTYPELIST_RECEIVE]: () => ({ ...emptyTransactionType }),
    [MODIFY_JOBTYPE_RECEIVE]: () => ({ ...emptyTransactionType }),
    [CREATE_JOBTYPE_RECEIVE]: () => ({ ...emptyTransactionType }),
    [PAYMENTTYPES_RECEIVE]: () => ({ ...emptyTransactionType }),
    [MODIFY_PAYMENTTYPE_RECEIVE]: () => ({ ...emptyTransactionType }),
    [CREATE_PAYMENTTYPE_RECEIVE]: () => ({ ...emptyTransactionType }),
});

export default transactiontypeReducer;
