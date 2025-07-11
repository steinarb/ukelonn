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
import bonus from './bonusSlice';
import jobIdsSelectedForDelete from './jobIdsSelectedForDeleteReducer';
import locale from './localeReducer';

export default (basename) => combineReducers({
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
    bonus,
    jobIdsSelectedForDelete,
    basename: createReducer(basename, (builder) => builder),
});
