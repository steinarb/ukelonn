
export const bankAccount = 4;

export const emptyPerformedTransaction = {
    transactionTypeId: -1,
    transactionAmount: 0.0,
    transactionDate: new Date().toISOString(),
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
    transactionTime: new Date().toISOString(),
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
