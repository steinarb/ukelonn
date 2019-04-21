import { createReducer } from 'redux-starter-kit';
import {
    JOBTYPELIST_RECEIVE,
    MODIFY_JOBTYPE_RECEIVE,
    CREATE_JOBTYPE_RECEIVE,
} from '../actiontypes';
import { emptyTransactionType } from './constants';

const jobtypesReducer = createReducer([], {
    [JOBTYPELIST_RECEIVE]: (state, action) => addEmptyTransactionTypeToJobtypeslist(action),
    [MODIFY_JOBTYPE_RECEIVE]: (state, action) => addEmptyTransactionTypeToJobtypeslist(action),
    [CREATE_JOBTYPE_RECEIVE]: (state, action) => addEmptyTransactionTypeToJobtypeslist(action),
});

export default jobtypesReducer;

function addEmptyTransactionTypeToJobtypeslist(action) {
    if (!action.payload.find((job) => job.id === -1)) {
        action.payload.unshift(emptyTransactionType);
    }
    return action.payload;
}
