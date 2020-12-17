import { fork } from 'redux-saga/effects';
import { requestInitialLoginStateSaga, requestLoginSaga } from './loginSaga';
import { requestLogoutSaga } from './logoutSaga';
import locationSaga from './locationSaga';
import accountSaga from './accountSaga';
import { requestJobtypeListSaga } from './jobtypelistSaga';
import { requestRegisterJobSaga } from './registerjobSaga';
import { requestRecentJobsSaga } from './recentjobsSaga';
import { requestRecentPaymentsSaga } from  './recentpaymentsSaga';
import { requestAccountsSaga } from './accountsSaga';
import { requestPaymenttypesSaga } from './paymenttypesSaga';
import { requestRegisterPaymentSaga } from './registerpaymentSaga';
import { requestModifyJobtypeSaga } from './modifyjobtypeSaga';
import { requestCreatePaymenttypeSaga } from './createpaymenttypeSaga';
import { requestCreateJobtypeSaga } from './createjobtypeSaga';
import { requestDeleteJobsSaga } from './deletejobsSaga';
import { requestUpdateJobSaga } from './updatejobSaga';
import { requestModifyPaymenttypeSaga } from './modifypaymenttypeSaga';
import { requestUsersSaga } from './usersSaga';
import { requestModifyUserSaga } from './modifyuserSaga';
import { requestCreateUserSaga } from './createuserSaga';
import adminstatusSaga from './adminstatusSaga';
import changeadminstatusSaga from './changeadminstatusSaga';
import { requestActivebonusesSaga } from './activebonusesSaga';
import { requestAllbonusesSaga } from './allbonusesSaga';
import modifybonusSaga from './modifybonusSaga';
import createbonusSaga from './createbonusSaga';
import deletebonusSaga from './deletebonusSaga';
import { requestChangePasswordSaga } from './modifyuserpasswordSaga';
import { startNotificationListening } from './notificationSaga';
import earningsSumOverYearSaga from './earningsSumOverYearSaga';
import earningsSumOverMonthSaga from './earningsSumOverMonthSaga';
import defaultLocaleSaga from './defaultLocaleSaga';
import availableLocalesSaga from './availableLocalesSaga';
import displayTextsSaga from './displayTextsSaga';

export function* rootSaga() {
    yield [
        fork(requestInitialLoginStateSaga),
        fork(requestLoginSaga),
        fork(requestLogoutSaga),
        fork(locationSaga),
        fork(accountSaga),
        fork(requestJobtypeListSaga),
        fork(requestRegisterJobSaga),
        fork(requestRecentJobsSaga),
        fork(requestRecentPaymentsSaga),
        fork(requestAccountsSaga),
        fork(requestPaymenttypesSaga),
        fork(requestRegisterPaymentSaga),
        fork(requestModifyJobtypeSaga),
        fork(requestCreateJobtypeSaga),
        fork(requestDeleteJobsSaga),
        fork(requestUpdateJobSaga),
        fork(requestModifyPaymenttypeSaga),
        fork(requestCreatePaymenttypeSaga),
        fork(requestUsersSaga),
        fork(requestModifyUserSaga),
        fork(adminstatusSaga),
        fork(changeadminstatusSaga),
        fork(requestActivebonusesSaga),
        fork(requestAllbonusesSaga),
        fork(modifybonusSaga),
        fork(createbonusSaga),
        fork(deletebonusSaga),
        fork(requestCreateUserSaga),
        fork(requestChangePasswordSaga),
        fork(startNotificationListening),
        fork(earningsSumOverYearSaga),
        fork(earningsSumOverMonthSaga),
        fork(defaultLocaleSaga),
        fork(availableLocalesSaga),
        fork(displayTextsSaga),
    ];
};
