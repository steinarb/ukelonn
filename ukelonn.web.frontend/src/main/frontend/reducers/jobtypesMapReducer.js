import { createReducer } from 'redux-starter-kit';
import {
    JOBTYPELIST_RECEIVE,
    MODIFY_JOBTYPE_RECEIVE,
    CREATE_JOBTYPE_RECEIVE,
} from '../actiontypes';
import { emptyTransactionType } from './constants';

const jobtypesMapReducer = createReducer(new Map([]), {
    [JOBTYPELIST_RECEIVE]: (state, action) => createMapFromJobtypelist(action),
    [MODIFY_JOBTYPE_RECEIVE]: (state, action) => createMapFromJobtypelist(action),
    [CREATE_JOBTYPE_RECEIVE]: (state, action) => createMapFromJobtypelist(action),
});

export default jobtypesMapReducer;

function createMapFromJobtypelist(action) {
    if (!action.payload.find((job) => job.id === -1)) {
        action.payload.unshift(emptyTransactionType);
    }
    return new Map(action.payload.map(i => [i.transactionTypeName, i]));
}
