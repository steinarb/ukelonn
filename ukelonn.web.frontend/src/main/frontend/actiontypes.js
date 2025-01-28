import { createAction } from '@reduxjs/toolkit';

export const UPDATE_FIRSTTIMEAFTERLOGIN = createAction('UPDATE_FIRSTTIMEAFTERLOGIN');
export const MODIFY_USER_IS_ADMINISTRATOR = createAction('MODIFY_USER_IS_ADMINISTRATOR');
export const MODIFY_PASSWORD1 = createAction('MODIFY_PASSWORD1');
export const MODIFY_PASSWORD2 = createAction('MODIFY_PASSWORD2');
export const MODIFY_PASSWORDS_NOT_IDENTICAL = createAction('MODIFY_PASSWORDS_NOT_IDENTICAL');
export const CLEAR_USER_AND_PASSWORDS = createAction('CLEAR_USER_AND_PASSWORDS');
export const MODIFY_JOB_DATE = createAction('MODIFY_JOB_DATE');
export const SAVE_CHANGES_TO_JOB_TYPE_BUTTON_CLICKED = createAction('SAVE_CHANGES_TO_JOB_TYPE_BUTTON_CLICKED');
export const JOB_TABLE_ROW_CLICK = createAction('JOB_TABLE_ROW_CLICK');
export const MODIFY_MARK_JOB_FOR_DELETE = createAction('MODIFY_MARK_JOB_FOR_DELETE');
export const UPDATE_NOTIFICATIONAVAILABLE = createAction('UPDATE_NOTIFICATIONAVAILABLE');
export const RELOAD_WEB_PAGE = createAction('RELOAD_WEB_PAGE');
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
export const UPDATE_LOCALE = createAction('UPDATE_LOCALE');

export const REST_API_FAILURE_UNAUTHORIZED = createAction('REST_API_FAILURE_UNAUTHORIZED');
export const REST_API_FAILURE_FORBIDDEN = createAction('REST_API_FAILURE_FORBIDDEN');
