const emptyPerformedTransaction = {
    account: { id: -1 },
    transactionTypeId: -1,
    transactionAmount: 0.0
};


export const ukelonnReducer = (state =
                               { username: null,
                                 password: null,
                                 account: { firstName: 'Ukjent', fullName: '', balance: 0.0 },
                                 paymenttype: { id: -1, transactionTypeName: '', transactionAmount: 0.0, transactionIsWork: false, transactionIsWagePayment: true },
                                 payment: {...emptyPerformedTransaction},
                                 jobs: [],
                                 payments: [],
                                 jobtypes: [],
                                 loginResponse: {
                                     username: '',
                                     roles: [],
                                     error: ''
                                 },
                                 performedjob: {...emptyPerformedTransaction},
                                 accounts: [],
                                 paymenttypes: [],
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

    if (action.type === 'ACCOUNT_RECEIVE') {
        return {
            ...state,
            account: action.account
        };
    }

    if (action.type === 'REGISTERJOB_RECEIVE') {
        return {
            ...state,
            performedjob: {...emptyPerformedTransaction},
            account: action.account
        };
    }

    if (action.type === 'REGISTERPAYMENT_RECEIVE') {
        return {
            ...state,
            payment: {...emptyPerformedTransaction},
            account: action.account
        };
    }

    if (action.type === 'RECENTJOBS_RECEIVE') {
        return {
            ...state,
            jobs: action.jobs
        };
    }

    if (action.type === 'RECENTPAYMENTS_RECEIVE') {
        return {
            ...state,
            payments: action.payments
        };
    }

    if (action.type === 'JOBTYPELIST_RECEIVE') {
        return {
            ...state,
            jobtypes: action.jobtypes
        };
    }

    if (action.type === 'ACCOUNTS_RECEIVE') {
        return {
            ...state,
            accounts: action.accounts
        };
    }

    if (action.type === 'PAYMENTTYPES_RECEIVE') {
        return {
            ...state,
            paymenttype: action.paymenttype,
            paymenttypes: action.paymenttypes,
        };
    }

    return { ...state };
};
