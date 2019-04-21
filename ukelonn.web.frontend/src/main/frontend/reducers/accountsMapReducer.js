import { createReducer } from 'redux-starter-kit';
import {
    ACCOUNTS_RECEIVE,
} from '../actiontypes';
import { emptyAccount } from './constants';

const accountsMapReducer = createReducer(new Map([]), {
    [ACCOUNTS_RECEIVE]: (state, action) => createMapFromAccounts(action),
});

export default accountsMapReducer;

function createMapFromAccounts(action) {
    if (!action.payload.find((account) => account.accountId === -1)) {
        action.payload.unshift(emptyAccount);
    }
    return new Map(action.payload.map(i => [i.fullName, i]));
}
