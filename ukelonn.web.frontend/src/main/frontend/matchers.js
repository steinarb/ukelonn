import { isAnyOf } from '@reduxjs/toolkit';
import { api } from './api';
import {
} from './actiontypes';

export const isClearTransactionTypeForm = isAnyOf(
    api.endpoints.postJobtypeModify.matchFulfilled,
    api.endpoints.postJobtypeCreate.matchFulfilled,
    api.endpoints.postJobUpdate.matchFulfilled,
    api.endpoints.postPaymenttypeModify.matchFulfilled,
    api.endpoints.postPaymenttypeCreate.matchFulfilled,
);

export const isJobsLoaded = isAnyOf(
    api.endpoints.getJobs.matchFulfilled,
    api.endpoints.postJobsDelete.matchFulfilled,
    api.endpoints.postJobUpdate.matchFulfilled,
);

export const isUsersLoaded = isAnyOf(
    api.endpoints.getUsers.matchFulfilled,
    api.endpoints.postUserModify.matchFulfilled,
    api.endpoints.postUserPassword.matchFulfilled,
    api.endpoints.postUserCreate.matchFulfilled,
);

export const isAllbonusesLoaded = isAnyOf(
    api.endpoints.getAllbonuses.matchFulfilled,
    api.endpoints.postModifybonus.matchFulfilled,
    api.endpoints.postCreatebonus.matchFulfilled,
    api.endpoints.postDeletebonus.matchFulfilled,
);

export const isRejectedRequest = isAnyOf(
    api.endpoints.getAccount.matchRejected,
    api.endpoints.getJobtypes.matchRejected,
    api.endpoints.getActivebonuses.matchRejected,
    api.endpoints.getJobs.matchRejected,
    api.endpoints.getPayments.matchRejected,
    api.endpoints.getSumoveryear.matchRejected,
    api.endpoints.getSumovermonth.matchRejected,
    api.endpoints.getAccounts.matchRejected,
    api.endpoints.getPaymenttypes.matchRejected,
    api.endpoints.getUsers.matchRejected,
    api.endpoints.getAllbonuses.matchRejected,
    api.endpoints.postUserAdminstatus.matchRejected,
    api.endpoints.postJobRegister.matchRejected,
    api.endpoints.postPaymentRegister.matchRejected,
    api.endpoints.postJobtypeModify.matchRejected,
    api.endpoints.postJobtypeCreate.matchRejected,
    api.endpoints.postJobsDelete.matchRejected,
    api.endpoints.postJobUpdate.matchRejected,
    api.endpoints.postPaymenttypeModify.matchRejected,
    api.endpoints.postPaymenttypeCreate.matchRejected,
    api.endpoints.postUserModify.matchRejected,
    api.endpoints.postUserChangeadminstatus.matchRejected,
    api.endpoints.postUserPassword.matchRejected,
    api.endpoints.postUserCreate.matchRejected,
    api.endpoints.postModifybonus.matchRejected,
    api.endpoints.postCreatebonus.matchRejected,
    api.endpoints.postDeletebonus.matchRejected,
);
