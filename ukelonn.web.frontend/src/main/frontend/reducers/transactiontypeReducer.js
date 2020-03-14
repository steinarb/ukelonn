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
    [JOBTYPELIST_RECEIVE]: (state, action) => ({ ...emptyTransactionType }),
    [MODIFY_JOBTYPE_RECEIVE]: (state, action) => ({ ...emptyTransactionType }),
    [CREATE_JOBTYPE_RECEIVE]: (state, action) => ({ ...emptyTransactionType }),
    [PAYMENTTYPES_RECEIVE]: (state, action) => ({ ...emptyTransactionType }),
    [MODIFY_PAYMENTTYPE_RECEIVE]: (state, action) => ({ ...emptyTransactionType }),
    [CREATE_PAYMENTTYPE_RECEIVE]: (state, action) => ({ ...emptyTransactionType }),
});

export default transactiontypeReducer;
