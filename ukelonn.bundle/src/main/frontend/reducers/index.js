export const ukelonnReducer = (state =
                               { username: null,
                                 password: null,
                                 account: { firstName: 'Ukjent' },
                                 jobtypes: [],
                                 loginResponse: {
                                     username: '',
                                     roles: [],
                                     error: ''
                                 },
                                 performedjob: {
                                     account: { id: -1 },
                                     transactionTypeId: -1,
                                     transactionAmount: 0.0
                                 },
                               },
                               action) => {
    if (action.type == 'UPDATE') {
        return {
            ...state,
            ...action.data
        };
    }

    if (action.type === 'LOGIN_REQUEST' || action.type === 'LOGOUT_REQUEST' || action.type === 'INITIAL_LOGIN_STATE_REQUEST') {
        return {
            ...state
        };
    }

    if (action.type === 'LOGIN_RECEIVE' || action.type === 'LOGOUT_RECEIVE' || action.type === 'INITIAL_LOGIN_STATE_RECEIVE') {
        return {
            ...state,
            loginResponse: action.loginResponse
        };
    }

    if (action.type === 'ACCOUNT_RECEIVE' || action.type === 'REGISTERJOB_RECEIVE') {
        return {
            ...state,
            account: action.account
        };
    }

    if (action.type === 'JOBTYPELIST_RECEIVE') {
        return {
            ...state,
            jobtypes: action.jobtypes
        };
    }

    return { ...state };
};
