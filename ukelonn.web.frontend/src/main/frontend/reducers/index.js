import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import notificationAvailable from './notificationAvailableReducer';
import notificationMessage from './notificationMessageReducer';
import accountId from './accountIdReducer';
import accountUsername from './accountUsernameReducer';
import accountFirstname from './accountFirstnameReducer';
import accountLastname from './accountLastnameReducer';
import accountBalance from './accountBalanceReducer';
import accountFullname from './accountFullnameReducer';
import jobs from './jobsReducer';
import payments from './paymentsReducer';
import jobtypes from './jobtypesReducer';
import haveReceivedResponseFromLogin from './haveReceivedResponseFromLoginReducer';
import loginResponse from './loginResponseReducer';
import transactionId from './transactionIdReducer';
import transactionTypeId from './transactionTypeIdReducer';
import transactionTypeName from './transactionTypeNameReducer';
import transactionAmount from './transactionAmountReducer';
import transactionDate from './transactionDateReducer';
import accounts from './accountsReducer';
import paymenttypes from './paymenttypesReducer';
import users from './usersReducer';
import usernames from './usernamesReducer';
import password1 from './password1Reducer';
import password2 from './password2Reducer';
import passwordsNotIdentical from './passwordsNotIdenticalReducer';
import userid from './useridReducer';
import userUsername from './userUsernameReducer';
import userEmail from './userEmailReducer';
import userFirstname from './userFirstnameReducer';
import userLastname from './userLastnameReducer';
import userIsAdministrator from './userIsAdministratorReducer';
import activebonuses from './activebonusesReducer';
import allbonuses from './allbonusesReducer';
import bonusId from './bonusIdReducer';
import bonusEnabled from './bonusEnabledReducer';
import bonusIconurl from './bonusIconurlReducer';
import bonusTitle from './bonusTitleReducer';
import bonusDescription from './bonusDescriptionReducer';
import bonusFactor from './bonusFactorReducer';
import bonusStartDate from './bonusStartDateReducer';
import bonusEndDate from './bonusEndDateReducer';
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
    notificationAvailable,
    notificationMessage,
    accountId,
    accountUsername,
    accountFirstname,
    accountLastname,
    accountBalance,
    accountFullname,
    jobs,
    payments,
    jobtypes,
    haveReceivedResponseFromLogin,
    loginResponse,
    transactionId,
    transactionTypeId,
    transactionTypeName,
    transactionAmount,
    transactionDate,
    accounts,
    paymenttypes,
    users,
    usernames,
    password1,
    password2,
    passwordsNotIdentical,
    userid,
    userUsername,
    userEmail,
    userFirstname,
    userLastname,
    userIsAdministrator,
    activebonuses,
    allbonuses,
    bonusId,
    bonusEnabled,
    bonusIconurl,
    bonusTitle,
    bonusDescription,
    bonusFactor,
    bonusStartDate,
    bonusEndDate,
    earningsSumOverYear,
    earningsSumOverMonth,
});
