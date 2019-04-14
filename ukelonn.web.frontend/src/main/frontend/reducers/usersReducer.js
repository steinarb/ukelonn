import { createReducer } from 'redux-starter-kit';
import {
    USERS_RECEIVE,
    MODIFY_USER_RECEIVE,
    CREATE_USER_RECEIVE,
    MODIFY_USER_PASSWORD_RECEIVE,
} from '../actiontypes';
import { emptyUser } from './constants';

const usersReducer = createReducer([], {
    [USERS_RECEIVE]: (state, action) => addFullnameAndEmptyTransactionTypeToUserslist(action),
    [MODIFY_USER_RECEIVE]: (state, action) => addFullnameAndEmptyTransactionTypeToUserslist(action),
    [CREATE_USER_RECEIVE]: (state, action) => addFullnameAndEmptyTransactionTypeToUserslist(action),
    [MODIFY_USER_PASSWORD_RECEIVE]: (state, action) => addFullnameAndEmptyTransactionTypeToUserslist(action),
});

export default usersReducer;

function addFullnameAndEmptyTransactionTypeToUserslist(action) {
    const users = addFullnameToUsers(action.payload);
    if (!users.find((job) => job.id === -1)) {
        users.unshift(emptyUser);
    }
    return users;
}

function addFullnameToUsers(users) {
    return users.map(user => {
        const fullname = user.firstname + ' ' + user.lastname;
        return { ...user, fullname };
    });
}
