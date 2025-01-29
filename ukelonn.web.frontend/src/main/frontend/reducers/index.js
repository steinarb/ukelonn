import { combineReducers } from 'redux';
import { createReducer } from '@reduxjs/toolkit';
import { api } from '../api';
import notificationAvailable from './notificationAvailableReducer';
import account from './accountSlice';
import haveReceivedResponseFromLogin from './haveReceivedResponseFromLoginReducer';
import loginResponse from './loginResponseReducer';
import transaction from './transactionSlice';
import transactionType from './transactionTypeSlice';
import usernames from './usernamesReducer';
import user from './userSlice';
import userIsAdministrator from './userIsAdministratorReducer';
import password from './passwordSlice';
import bonusId from './bonusIdReducer';
import bonusEnabled from './bonusEnabledReducer';
import bonusIconurl from './bonusIconurlReducer';
import bonusTitle from './bonusTitleReducer';
import bonusDescription from './bonusDescriptionReducer';
import bonusFactor from './bonusFactorReducer';
import bonusStartDate from './bonusStartDateReducer';
import bonusEndDate from './bonusEndDateReducer';
import jobIdsSelectedForDelete from './jobIdsSelectedForDeleteReducer';
import locale from './localeReducer';

export default (routerReducer, basename) => combineReducers({
    router: routerReducer,
    [api.reducerPath]: api.reducer,
    locale,
    notificationAvailable,
    account,
    haveReceivedResponseFromLogin,
    loginResponse,
    transaction,
    transactionType,
    usernames,
    user,
    userIsAdministrator,
    password,
    bonusId,
    bonusEnabled,
    bonusIconurl,
    bonusTitle,
    bonusDescription,
    bonusFactor,
    bonusStartDate,
    bonusEndDate,
    jobIdsSelectedForDelete,
    basename: createReducer(basename, (builder) => builder),
});
