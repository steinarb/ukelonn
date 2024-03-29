import { createAction } from '@reduxjs/toolkit';

export const UPDATE_FIRSTTIMEAFTERLOGIN = createAction('UPDATE_FIRSTTIMEAFTERLOGIN');
export const SELECT_USER = createAction('SELECT_USER');
export const MODIFY_USER_USERNAME = createAction('MODIFY_USER_USERNAME');
export const MODIFY_USER_EMAIL = createAction('MODIFY_USER_EMAIL');
export const MODIFY_USER_FIRSTNAME = createAction('MODIFY_USER_FIRSTNAME');
export const MODIFY_USER_LASTNAME = createAction('MODIFY_USER_LASTNAME');
export const MODIFY_USER_IS_ADMINISTRATOR = createAction('MODIFY_USER_IS_ADMINISTRATOR');
export const CLEAR_USER = createAction('CLEAR_USER');
export const SAVE_USER_BUTTON_CLICKED = createAction('SAVE_USER_BUTTON_CLICKED');
export const CREATE_USER_BUTTON_CLICKED = createAction('CREATE_USER_BUTTON_CLICKED');
export const MODIFY_PASSWORD1 = createAction('MODIFY_PASSWORD1');
export const MODIFY_PASSWORD2 = createAction('MODIFY_PASSWORD2');
export const MODIFY_PASSWORDS_NOT_IDENTICAL = createAction('MODIFY_PASSWORDS_NOT_IDENTICAL');
export const CHANGE_PASSWORD_BUTTON_CLICKED = createAction('CHANGE_PASSWORD_BUTTON_CLICKED');
export const CLEAR_USER_AND_PASSWORDS = createAction('CLEAR_USER_AND_PASSWORDS');
export const SELECT_ACCOUNT = createAction('SELECT_ACCOUNT');
export const SELECT_JOB_TYPE = createAction('SELECT_JOB_TYPE');
export const SELECTED_JOB_TYPE = createAction('SELECTED_JOB_TYPE');
export const MODIFY_JOB_AMOUNT = createAction('MODIFY_JOB_AMOUNT');
export const MODIFY_JOB_DATE = createAction('MODIFY_JOB_DATE');
export const REGISTER_JOB_BUTTON_CLICKED = createAction('REGISTER_JOB_BUTTON_CLICKED');
export const SAVE_CHANGES_TO_JOB_BUTTON_CLICKED = createAction('SAVE_CHANGES_TO_JOB_BUTTON_CLICKED');
export const CLEAR_JOB_FORM = createAction('CLEAR_JOB_FORM');
export const SELECT_PAYMENT_TYPE = createAction('SELECT_PAYMENT_TYPE');
export const SELECT_PAYMENT_TYPE_FOR_EDIT = createAction('SELECT_PAYMENT_TYPE_FOR_EDIT');
export const SELECTED_PAYMENT_TYPE = createAction('SELECTED_PAYMENT_TYPE');
export const MODIFY_PAYMENT_AMOUNT = createAction('MODIFY_PAYMENT_AMOUNT');
export const MODIFY_TRANSACTION_TYPE_NAME = createAction('MODIFY_TRANSACTION_TYPE_NAME');
export const CLEAR_REGISTER_JOB_FORM = createAction('CLEAR_REGISTER_JOB_TYPE_FORM');
export const SAVE_CHANGES_TO_JOB_TYPE_BUTTON_CLICKED = createAction('SAVE_CHANGES_TO_JOB_TYPE_BUTTON_CLICKED');
export const CREATE_NEW_JOB_TYPE_BUTTON_CLICKED = createAction('CREATE_NEW_JOB_TYPE_BUTTON_CLICKED');
export const CLEAR_JOB_TYPE_FORM = createAction('CLEAR_JOB_TYPE_FORM');
export const CLEAR_JOB_TYPE_CREATE_FORM = createAction('CLEAR_JOB_TYPE_CREATE_FORM');
export const JOB_TABLE_ROW_CLICK = createAction('JOB_TABLE_ROW_CLICK');
export const REGISTER_PAYMENT_BUTTON_CLICKED = createAction('REGISTER_PAYMENT_BUTTON_CLICKED');
export const SAVE_CHANGES_TO_PAYMENT_TYPE_BUTTON_CLICKED = createAction('SAVE_CHANGES_TO_PAYMENT_TYPE_BUTTON_CLICKED');
export const CREATE_PAYMENT_TYPE_BUTTON_CLICKED = createAction('CREATE_PAYMENT_TYPE_BUTTON_CLICKED');
export const CLEAR_PAYMENT_TYPE_FORM = createAction('CLEAR_PAYMENT_TYPE_FORM');
export const CLEAR_EDIT_JOB_FORM = createAction('CLEAR_EDIT_JOB_FORM');
export const MODIFY_MARK_JOB_FOR_DELETE = createAction('MODIFY_MARK_JOB_FOR_DELETE');
export const DELETE_SELECTED_JOBS_BUTTON_CLICKED = createAction('DELETE_SELECTED_JOBS_BUTTON_CLICKED');
export const UPDATE_NOTIFICATIONMESSAGE = createAction('UPDATE_NOTIFICATIONMESSAGE');
export const UPDATE_NOTIFICATIONAVAILABLE = createAction('UPDATE_NOTIFICATIONAVAILABLE');
export const INITIAL_LOGIN_STATE_REQUEST = createAction('INITIAL_LOGIN_STATE_REQUEST');
export const INITIAL_LOGIN_STATE_RECEIVE = createAction('INITIAL_LOGIN_STATE_RECEIVE');
export const INITIAL_LOGIN_STATE_FAILURE = createAction('INITIAL_LOGIN_STATE_FAILURE');
export const CHECK_LOGIN_STATE_REQUEST = createAction('CHECK_LOGIN_STATE_REQUEST');
export const CHECK_LOGIN_STATE_RECEIVE = createAction('CHECK_LOGIN_STATE_RECEIVE');
export const CHECK_LOGIN_STATE_FAILURE = createAction('CHECK_LOGIN_STATE_FAILURE');
export const LOGIN_REQUEST = createAction('LOGIN_REQUEST');
export const LOGIN_RECEIVE = createAction('LOGIN_RECEIVE');
export const LOGIN_FAILURE = createAction('LOGIN_FAILURE');
export const LOGOUT_REQUEST = createAction('LOGOUT_REQUEST');
export const LOGOUT_RECEIVE = createAction('LOGOUT_RECEIVE');
export const LOGOUT_FAILURE = createAction('LOGOUT_FAILURE');
export const RELOAD_WEB_PAGE = createAction('RELOAD_WEB_PAGE');
export const ACCOUNT_REQUEST = createAction('ACCOUNT_REQUEST');
export const ACCOUNT_RECEIVE = createAction('ACCOUNT_RECEIVE');
export const ACCOUNT_FAILURE = createAction('ACCOUNT_FAILURE');
export const JOBTYPELIST_REQUEST = createAction('JOBTYPELIST_REQUEST');
export const JOBTYPELIST_RECEIVE = createAction('JOBTYPELIST_RECEIVE');
export const JOBTYPELIST_FAILURE = createAction('JOBTYPELIST_FAILURE');
export const REGISTERJOB_REQUEST = createAction('REGISTERJOB_REQUEST');
export const REGISTERJOB_RECEIVE = createAction('REGISTERJOB_RECEIVE');
export const REGISTERJOB_FAILURE = createAction('REGISTERJOB_FAILURE');
export const RECENTJOBS_REQUEST = createAction('RECENTJOBS_REQUEST');
export const RECENTJOBS_RECEIVE = createAction('RECENTJOBS_RECEIVE');
export const RECENTJOBS_FAILURE = createAction('RECENTJOBS_FAILURE');
export const RECENTPAYMENTS_REQUEST = createAction('RECENTPAYMENTS_REQUEST');
export const RECENTPAYMENTS_RECEIVE = createAction('RECENTPAYMENTS_RECEIVE');
export const RECENTPAYMENTS_FAILURE = createAction('RECENTPAYMENTS_FAILURE');
export const ACCOUNTS_REQUEST = createAction('ACCOUNTS_REQUEST');
export const ACCOUNTS_RECEIVE = createAction('ACCOUNTS_RECEIVE');
export const ACCOUNTS_FAILURE = createAction('ACCOUNTS_FAILURE');
export const PAYMENTTYPES_REQUEST = createAction('PAYMENTTYPES_REQUEST');
export const PAYMENTTYPES_RECEIVE = createAction('PAYMENTTYPES_RECEIVE');
export const PAYMENTTYPES_FAILURE = createAction('PAYMENTTYPES_FAILURE');
export const REGISTERPAYMENT_REQUEST = createAction('REGISTERPAYMENT_REQUEST');
export const REGISTERPAYMENT_RECEIVE = createAction('REGISTERPAYMENT_RECEIVE');
export const REGISTERPAYMENT_FAILURE = createAction('REGISTERPAYMENT_FAILURE');
export const MODIFY_JOBTYPE_REQUEST = createAction('MODIFY_JOBTYPE_REQUEST');
export const MODIFY_JOBTYPE_RECEIVE = createAction('MODIFY_JOBTYPE_RECEIVE');
export const MODIFY_JOBTYPE_FAILURE = createAction('MODIFY_JOBTYPE_FAILURE');
export const CREATE_JOBTYPE_REQUEST = createAction('CREATE_JOBTYPE_REQUEST');
export const CREATE_JOBTYPE_RECEIVE = createAction('CREATE_JOBTYPE_RECEIVE');
export const CREATE_JOBTYPE_FAILURE = createAction('CREATE_JOBTYPE_FAILURE');
export const DELETE_JOBS_REQUEST = createAction('DELETE_JOBS_REQUEST');
export const DELETE_JOBS_RECEIVE = createAction('DELETE_JOBS_RECEIVE');
export const DELETE_JOBS_FAILURE = createAction('DELETE_JOBS_FAILURE');
export const UPDATE_JOB_REQUEST = createAction('UPDATE_JOB_REQUEST');
export const UPDATE_JOB_RECEIVE = createAction('UPDATE_JOB_RECEIVE');
export const UPDATE_JOB_FAILURE = createAction('UPDATE_JOB_FAILURE');
export const MODIFY_PAYMENTTYPE_REQUEST = createAction('MODIFY_PAYMENTTYPE_REQUEST');
export const MODIFY_PAYMENTTYPE_RECEIVE = createAction('MODIFY_PAYMENTTYPE_RECEIVE');
export const MODIFY_PAYMENTTYPE_FAILURE = createAction('MODIFY_PAYMENTTYPE_FAILURE');
export const CREATE_PAYMENTTYPE_REQUEST = createAction('CREATE_PAYMENTTYPE_REQUEST');
export const CREATE_PAYMENTTYPE_RECEIVE = createAction('CREATE_PAYMENTTYPE_RECEIVE');
export const CREATE_PAYMENTTYPE_FAILURE = createAction('CREATE_PAYMENTTYPE_FAILURE');
export const USERS_REQUEST = createAction('USERS_REQUEST');
export const USERS_RECEIVE = createAction('USERS_RECEIVE');
export const USERS_FAILURE = createAction('USERS_FAILURE');
export const CHANGE_USER_REQUEST = createAction('CHANGE_USER_REQUEST');
export const CHANGE_USER_RECEIVE = createAction('CHANGE_USER_RECEIVE');
export const CHANGE_USER_FAILURE = createAction('CHANGE_USER_FAILURE');
export const CREATE_USER_REQUEST = createAction('CREATE_USER_REQUEST');
export const CREATE_USER_RECEIVE = createAction('CREATE_USER_RECEIVE');
export const CREATE_USER_FAILURE = createAction('CREATE_USER_FAILURE');
export const CHANGE_USER_PASSWORD_REQUEST = createAction('CHANGE_USER_PASSWORD_REQUEST');
export const CHANGE_USER_PASSWORD_RECEIVE = createAction('CHANGE_USER_PASSWORD_RECEIVE');
export const CHANGE_USER_PASSWORD_FAILURE = createAction('CHANGE_USER_PASSWORD_FAILURE');
export const REQUEST_ADMIN_STATUS = createAction('REQUEST_ADMIN_STATUS');
export const RECEIVE_ADMIN_STATUS = createAction('RECEIVE_ADMIN_STATUS');
export const RECEIVE_ADMIN_STATUS_ERROR = createAction('RECEIVE_ADMIN_STATUS_ERROR');
export const CHANGE_ADMIN_STATUS = createAction('CHANGE_ADMIN_STATUS');
export const CHANGE_ADMIN_STATUS_RESPONSE = createAction('CHANGE_ADMIN_STATUS_RESPONSE');
export const CHANGE_ADMIN_STATUS_ERROR = createAction('CHANGE_ADMIN_STATUS_ERROR');
export const GET_ACTIVE_BONUSES = createAction('GET_ACTIVE_BONUSES');
export const RECEIVE_ACTIVE_BONUSES = createAction('RECEIVE_ACTIVE_BONUSES');
export const RECEIVE_ACTIVE_BONUSES_FAILURE = createAction('RECEIVE_ACTIVE_BONUSES_FAILURE');
export const GET_ALL_BONUSES = createAction('GET_ALL_BONUSES');
export const RECEIVE_ALL_BONUSES = createAction('RECEIVE_ALL_BONUSES');
export const RECEIVE_ALL_BONUSES_FAILURE = createAction('RECEIVE_ALL_BONUSES_FAILURE');
export const SELECT_BONUS = createAction('SELECT_BONUS');
export const SELECTED_BONUS = createAction('SELECTED_BONUS');
export const MODIFY_BONUS_ENABLED = createAction('MODIFY_BONUS_ENABLED');
export const MODIFY_BONUS_ICONURL = createAction('MODIFY_BONUS_ICONURL');
export const MODIFY_BONUS_TITLE = createAction('MODIFY_BONUS_TITLE');
export const MODIFY_BONUS_DESCRIPTION = createAction('MODIFY_BONUS_DESCRIPTION');
export const MODIFY_BONUS_FACTOR = createAction('MODIFY_BONUS_FACTOR');
export const MODIFY_BONUS_START_DATE = createAction('MODIFY_BONUS_START_DATE');
export const MODIFY_BONUS_END_DATE = createAction('MODIFY_BONUS_END_DATE');
export const CLEAR_BONUS = createAction('CLEAR_BONUS');
export const SAVE_BONUS_CHANGES_BUTTON_CLICKED = createAction('SAVE_BONUS_CHANGES_BUTTON_CLICKED');
export const CREATE_NEW_BONUS_BUTTON_CLICKED = createAction('CREATE_NEW_BONUS_BUTTON_CLICKED');
export const DELETE_SELECTED_BONUS_BUTTON_CLICKED = createAction('DELETE_SELECTED_BONUS_BUTTON_CLICKED');
export const CREATE_BONUS_REQUEST = createAction('CREATE_BONUS_REQUEST');
export const CREATE_BONUS_RECEIVE = createAction('CREATE_BONUS_RECEIVE');
export const CREATE_BONUS_FAILURE = createAction('CREATE_BONUS_FAILURE');
export const MODIFY_BONUS_REQUEST = createAction('MODIFY_BONUS_REQUEST');
export const MODIFY_BONUS_RECEIVE = createAction('MODIFY_BONUS_RECEIVE');
export const MODIFY_BONUS_FAILURE = createAction('MODIFY_BONUS_FAILURE');
export const DELETE_BONUS_REQUEST = createAction('DELETE_BONUS_REQUEST');
export const DELETE_BONUS_RECEIVE = createAction('DELETE_BONUS_RECEIVE');
export const DELETE_BONUS_FAILURE = createAction('DELETE_BONUS_FAILURE');
export const START_NOTIFICATION_LISTENING = createAction('START_NOTIFICATION_LISTENING');
export const RECEIVED_NOTIFICATION = createAction('RECEIVED_NOTIFICATION');
export const ERROR_RECEIVED_NOTIFICATION = createAction('ERROR_RECEIVED_NOTIFICATION');
export const EARNINGS_SUM_OVER_YEAR_REQUEST = createAction('EARNINGS_SUM_OVER_YEAR_REQUEST');
export const EARNINGS_SUM_OVER_YEAR_RECEIVE = createAction('EARNINGS_SUM_OVER_YEAR_RECEIVE');
export const EARNINGS_SUM_OVER_YEAR_FAILURE = createAction('EARNINGS_SUM_OVER_YEAR_FAILURE');
export const EARNINGS_SUM_OVER_MONTH_REQUEST = createAction('EARNINGS_SUM_OVER_MONTH_REQUEST');
export const EARNINGS_SUM_OVER_MONTH_RECEIVE = createAction('EARNINGS_SUM_OVER_MONTH_RECEIVE');
export const EARNINGS_SUM_OVER_MONTH_FAILURE = createAction('EARNINGS_SUM_OVER_MONTH_FAILURE');
export const DEFAULT_LOCALE_REQUEST = createAction('DEFAULT_LOCALE_REQUEST');
export const DEFAULT_LOCALE_RECEIVE = createAction('DEFAULT_LOCALE_RECEIVE');
export const DEFAULT_LOCALE_ERROR = createAction('DEFAULT_LOCALE_ERROR');
export const UPDATE_LOCALE = createAction('UPDATE_LOCALE');
export const AVAILABLE_LOCALES_REQUEST = createAction('AVAILABLE_LOCALES_REQUEST');
export const AVAILABLE_LOCALES_RECEIVE = createAction('AVAILABLE_LOCALES_RECEIVE');
export const AVAILABLE_LOCALES_ERROR = createAction('AVAILABLE_LOCALES_ERROR');
export const DISPLAY_TEXTS_REQUEST = createAction('DISPLAY_TEXTS_REQUEST');
export const DISPLAY_TEXTS_RECEIVE = createAction('DISPLAY_TEXTS_RECEIVE');
export const DISPLAY_TEXTS_ERROR = createAction('DISPLAY_TEXTS_ERROR');

export const REST_API_FAILURE_UNAUTHORIZED = createAction('REST_API_FAILURE_UNAUTHORIZED');
export const REST_API_FAILURE_FORBIDDEN = createAction('REST_API_FAILURE_FORBIDDEN');
