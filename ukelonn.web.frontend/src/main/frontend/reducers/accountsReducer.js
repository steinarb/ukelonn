import { createReducer } from 'redux-starter-kit';
import {
    ACCOUNTS_RECEIVE,
} from '../actiontypes';
import { emptyAccount } from './constants';

const accountsReducer = createReducer([], {
    [ACCOUNTS_RECEIVE]: (state, action) => addEmptyAccountToAccountslist(action),
});

export default accountsReducer;

function addEmptyAccountToAccountslist(action) {
    if (!action.payload.find((account) => account.accountId === -1)) {
        action.payload.unshift(emptyAccount);
    }
    return action.payload;
}
