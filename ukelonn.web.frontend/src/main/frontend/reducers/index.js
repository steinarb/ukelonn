import moment from 'moment';


const emptyPerformedTransaction = {
    account: { id: -1 },
    transactionTypeId: -1,
    transactionAmount: 0.0
};

const emptyTransactionType = {
    id: -1,
    transactionTypeName: '',
    transactionAmount: 0.0
};

const emptyUser = {
    userId: -1,
    fullName: '',
    username: '',
    email: '',
    firstname: '',
    lastname: '',
};

const emptyPasswords = {
    user: {...emptyUser},
    password: '',
    password2: '',
};

const emptyAccount = {
    accountId: -1,
    fullName: '',
    balance: 0.0,
};


export const ukelonnReducer = (state =
                               { username: null,
                                 password: null,
                                 firstTimeAfterLogin: false,
                                 notificationAvailable: false,
                                 account: { firstName: 'Ukjent', fullName: '', balance: 0.0 },
                                 paymenttype: { id: -1, transactionTypeName: '', transactionAmount: 0.0, transactionIsWork: false, transactionIsWagePayment: true },
                                 payment: {...emptyPerformedTransaction},
                                 jobs: [],
                                 payments: [],
                                 jobtypes: [],
                                 haveReceivedResponseFromLogin: false,
                                 loginResponse: {
                                     username: '',
                                     roles: [],
                                     error: ''
                                 },
                                 performedjob: {...emptyPerformedTransaction, transactionDate: moment()},
                                 accounts: [],
                                 accountsMap: {},
                                 paymenttypes: [],
                                 transactiontype: { ...emptyTransactionType },
                                 users: [],
                                 user: { ...emptyUser },
                                 passwords: {...emptyPasswords },
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
            firstTimeAfterLogin: true,
            haveReceivedResponseFromLogin: true,
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
            performedjob: {...emptyPerformedTransaction, transactionName: '', transactionDate: moment() },
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

    if (action.type === 'RECENTJOBS_RECEIVE' || action.type === 'DELETE_JOBS_RECEIVE') {
        action.jobs.map((job) => { job.delete=false; return job; });

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

    if (action.type === 'PAYMENTTYPELIST_RECEIVE') {
        return {
            ...state,
            paymenttypes: action.paymenttypes
        };
    }

    if (action.type === 'ACCOUNTS_RECEIVE') {
        if (!action.accounts.find((account) => account.accountId === -1)) {
            action.accounts.unshift(emptyAccount);
        }

        return {
            ...state,
            accounts: action.accounts,
            accountsMap: new Map(action.accounts.map(i => [i.fullName, i])),
        };
    }

    if (action.type === 'PAYMENTTYPES_RECEIVE') {
        return {
            ...state,
            paymenttype: action.paymenttype,
            paymenttypes: action.paymenttypes,
        };
    }

    if (action.type === 'MODIFY_JOBTYPE_RECEIVE' || action.type === 'CREATE_JOBTYPE_RECEIVE') {
        return {
            ...state,
            jobtypes: action.jobtypes,
            transactiontype: {...emptyTransactionType},
        };
    }

    if (action.type === 'MODIFY_PAYMENTTYPE_RECEIVE' || action.type === 'CREATE_PAYMENTTYPE_RECEIVE') {
        return {
            ...state,
            paymenttypes: action.paymenttypes,
            transactiontype: {...emptyTransactionType},
        };
    }

    if (action.type === 'USERS_RECEIVE') {
        const users = action.users;

        if (!users.find((user) => user.userId === -1)) {
            users.unshift(emptyUser);
        }

        return {
            ...state,
            users: users,
        };
    }

    if (action.type === 'MODIFY_USER_RECEIVE' || action.type === 'CREATE_USER_RECEIVE' || action.type === 'MODIFY_USER_PASSWORD_RECEIVE') {
        const users = action.users;

        if (!users.find((user) => user.userId === -1)) {
            users.unshift(emptyUser);
        }

        return {
            ...state,
            users: users,
            user: {...emptyUser},
            passwords: {...emptyPasswords},
        };
    }

    if (action.type === 'CLEAR_USER_AND_PASSWORD') {
        return {
            ...state,
            user: {...emptyUser},
            passwords: {...emptyPasswords},
        };
    }

    if (action.type === 'RECEIVED_NOTIFICATION') {
        return {
            notificationMessage: action.notifications[0],
            ...state,
        };
    }

    return { ...state };
};
