import moment from 'moment';

export const emptyPerformedTransaction = {
    account: { id: -1 },
    transactionTypeId: -1,
    transactionAmount: 0.0
};

export const emptyTransactionType = {
    id: -1,
    transactionTypeName: '',
    transactionAmount: 0.0
};

export const emptyTransaction = {
    id: -1,
    transactionType: { ...emptyTransactionType },
    transactionAmount: 0.0,
    transactionTime: moment(),
};

export const emptyUser = {
    userid: -1,
    fullname: '',
    username: '',
    email: '',
    firstname: '',
    lastname: '',
};

export const emptyPasswords = {
    user: {...emptyUser},
    password1: '',
    password2: '',
};

export const emptyAccount = {
    accountId: -1,
    username: '',
    firstName: '',
    lastName: '',
    fullName: '',
    balance: 0.0,
};
