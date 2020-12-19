import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import usernameReducer from './usernameReducer';
import passwordReducer from './passwordReducer';
import notificationAvailableReducer from './notificationAvailableReducer';
import notificationMessageReducer from './notificationMessageReducer';
import accountReducer from './accountReducer';
import paymentReducer from './paymentReducer';
import jobsReducer from './jobsReducer';
import paymentsReducer from './paymentsReducer';
import jobtypesReducer from './jobtypesReducer';
import haveReceivedResponseFromLoginReducer from './haveReceivedResponseFromLoginReducer';
import loginResponseReducer from './loginResponseReducer';
import performedjobReducer from './performedjobReducer';
import selectedjobReducer from './selectedjobReducer';
import accountsReducer from './accountsReducer';
import paymenttypesReducer from './paymenttypesReducer';
import transactiontypeReducer from './transactiontypeReducer';
import usersReducer from './usersReducer';
import usernamesReducer from './usernamesReducer';
import userReducer from './userReducer';
import passwordsReducer from './passwordsReducer';
import userIsAdministrator from './userIsAdministratorReducer';
import activebonusesReducer from './activebonusesReducer';
import allbonusesReducer from './allbonusesReducer';
import bonusReducer from './bonusReducer';
import earningsSumOverYearReducer from './earningsSumOverYearReducer';
import earningsSumOverMonthReducer from './earningsSumOverMonthReducer';
import locale from './localeReducer';
import availableLocales from './availableLocalesReducer';
import displayTexts from './displayTextsReducer';

export default (history) => combineReducers({
    router: connectRouter(history),
    locale,
    availableLocales,
    displayTexts,
    username: usernameReducer,
    password: passwordReducer,
    notificationAvailable: notificationAvailableReducer,
    notificationMessage: notificationMessageReducer,
    account: accountReducer,
    payment: paymentReducer,
    jobs: jobsReducer,
    payments: paymentsReducer,
    jobtypes: jobtypesReducer,
    haveReceivedResponseFromLogin: haveReceivedResponseFromLoginReducer,
    loginResponse: loginResponseReducer,
    performedjob: performedjobReducer,
    selectedjob: selectedjobReducer,
    accounts: accountsReducer,
    paymenttypes: paymenttypesReducer,
    transactiontype: transactiontypeReducer,
    users: usersReducer,
    usernames: usernamesReducer,
    user: userReducer,
    passwords: passwordsReducer,
    userIsAdministrator,
    activebonuses: activebonusesReducer,
    allbonuses: allbonusesReducer,
    bonus: bonusReducer,
    earningsSumOverYear: earningsSumOverYearReducer,
    earningsSumOverMonth: earningsSumOverMonthReducer,
});
