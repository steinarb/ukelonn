import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import username from './usernameReducer';
import password from './passwordReducer';
import notificationAvailable from './notificationAvailableReducer';
import notificationMessage from './notificationMessageReducer';
import account from './accountReducer';
import payment from './paymentReducer';
import jobs from './jobsReducer';
import payments from './paymentsReducer';
import jobtypes from './jobtypesReducer';
import haveReceivedResponseFromLogin from './haveReceivedResponseFromLoginReducer';
import loginResponse from './loginResponseReducer';
import performedjob from './performedjobReducer';
import selectedjob from './selectedjobReducer';
import accounts from './accountsReducer';
import paymenttypes from './paymenttypesReducer';
import transactiontype from './transactiontypeReducer';
import users from './usersReducer';
import usernames from './usernamesReducer';
import user from './userReducer';
import passwords from './passwordsReducer';
import userIsAdministrator from './userIsAdministratorReducer';
import activebonuses from './activebonusesReducer';
import allbonuses from './allbonusesReducer';
import bonus from './bonusReducer';
import earningsSumOverYear from './earningsSumOverYearReducer';
import earningsSumOverMonth from './earningsSumOverMonthReducer';
import locale from './localeReducer';
import availableLocales from './availableLocalesReducer';
import displayTexts from './displayTextsReducer';

export default (history) => combineReducers({
    router: connectRouter(history),
    locale,
    availableLocales,
    displayTexts,
    username,
    password,
    notificationAvailable,
    notificationMessage,
    account,
    payment,
    jobs,
    payments,
    jobtypes,
    haveReceivedResponseFromLogin,
    loginResponse,
    performedjob,
    selectedjob,
    accounts,
    paymenttypes,
    transactiontype,
    users,
    usernames,
    user,
    passwords,
    userIsAdministrator,
    activebonuses,
    allbonuses,
    bonus,
    earningsSumOverYear,
    earningsSumOverMonth,
});
