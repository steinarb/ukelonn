import { fork, all } from 'redux-saga/effects';
import { requestInitialLoginStateSaga, requestLoginSaga } from './loginSaga';
import { requestLogoutSaga } from './logoutSaga';
import locationSaga from './locationSaga';
import accountSaga from './accountSaga';
import { requestJobtypeListSaga } from './jobtypelistSaga';
import registerJobSaga from './registerJobSaga';
import jobSaga from './jobSaga';
import paymentSaga from './paymentSaga';
import { requestRecentJobsSaga } from './recentjobsSaga';
import { requestRecentPaymentsSaga } from  './recentpaymentsSaga';
import accountsSaga from './accountsSaga';
import paymenttypesSaga from './paymenttypesSaga';
import registerPaymentSaga from './registerPaymentSaga';
import modifyJobtypeSaga from './modifyJobtypeSaga';
import createPaymenttypeSaga from './createpaymenttypeSaga';
import createJobtypeSaga from './createJobtypeSaga';
import deleteJobsSaga from './deleteJobsSaga';
import updateJobSaga from './updateJobSaga';
import modifyPaymenttypeSaga from './modifyPaymenttypeSaga';
import usersSaga from './usersSaga';
import userSaga from './userSaga';
import modifyUserSaga from './modifyUserSaga';
import createUserSaga from './createUserSaga';
import adminstatusSaga from './adminstatusSaga';
import changeadminstatusSaga from './changeadminstatusSaga';
import { requestActivebonusesSaga } from './activebonusesSaga';
import { requestAllbonusesSaga } from './allbonusesSaga';
import bonusSaga from './bonusSaga';
import modifyBonusSaga from './modifyBonusSaga';
import createBonusSaga from './createBonusSaga';
import deleteBonusSaga from './deleteBonusSaga';
import changeUserPasswordSaga from './changeUserPasswordSaga';
import passwordSaga from './passwordSaga';
import { startNotificationListening } from './notificationSaga';
import earningsSumOverYearSaga from './earningsSumOverYearSaga';
import earningsSumOverMonthSaga from './earningsSumOverMonthSaga';
import defaultLocaleSaga from './defaultLocaleSaga';
import localeSaga from './localeSaga';
import availableLocalesSaga from './availableLocalesSaga';
import displayTextsSaga from './displayTextsSaga';

export function* rootSaga() {
    yield all([
        fork(requestInitialLoginStateSaga),
        fork(requestLoginSaga),
        fork(requestLogoutSaga),
        fork(locationSaga),
        fork(accountSaga),
        fork(requestJobtypeListSaga),
        fork(registerJobSaga),
        fork(jobSaga),
        fork(paymentSaga),
        fork(requestRecentJobsSaga),
        fork(requestRecentPaymentsSaga),
        fork(accountsSaga),
        fork(paymenttypesSaga),
        fork(registerPaymentSaga),
        fork(modifyJobtypeSaga),
        fork(createJobtypeSaga),
        fork(deleteJobsSaga),
        fork(updateJobSaga),
        fork(modifyPaymenttypeSaga),
        fork(createPaymenttypeSaga),
        fork(usersSaga),
        fork(userSaga),
        fork(modifyUserSaga),
        fork(adminstatusSaga),
        fork(changeadminstatusSaga),
        fork(requestActivebonusesSaga),
        fork(requestAllbonusesSaga),
        fork(bonusSaga),
        fork(modifyBonusSaga),
        fork(createBonusSaga),
        fork(deleteBonusSaga),
        fork(createUserSaga),
        fork(changeUserPasswordSaga),
        fork(passwordSaga),
        fork(startNotificationListening),
        fork(earningsSumOverYearSaga),
        fork(earningsSumOverMonthSaga),
        fork(defaultLocaleSaga),
        fork(localeSaga),
        fork(availableLocalesSaga),
        fork(displayTextsSaga),
    ]);
}
