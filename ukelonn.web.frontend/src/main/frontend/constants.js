
export const emptyAccount = {
    accountId: -1,
    username: '',
    firstname: '',
    lastname: '',
    fullName: '',
    balance: 0.0,
};

export const emptyUser = {
    userid: -1,
    username: '',
    email: '',
    firstname: '',
    lastname: '',
};

export const emptyBonus = {
    bonusId: -1,
    enabled: false,
    iconurl: '',
    title: '',
    description: '',
    bonusFactor: 1.0,
    startDate: new Date().toISOString(),
    endDate: new Date().toISOString(),
};
