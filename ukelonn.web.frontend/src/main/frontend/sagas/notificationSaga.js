import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import delay from 'delay';
import {
    START_NOTIFICATION_LISTENING,
    RECEIVED_NOTIFICATION,
    ERROR_RECEIVED_NOTIFICATION,
} from '../actiontypes';


// watcher saga
export function* startNotificationListening() {
    yield takeLatest(START_NOTIFICATION_LISTENING, pollNotification);
}

// worker saga
function* pollNotification(action) {
    const username = action.payload;
    const notificationsRestEndpoint = '/api/notificationsto/' + username;
    var loop = true;
    try {
        while (loop) {
            const response = yield call(() => axios({ url: notificationsRestEndpoint }));
            if (response.headers['content-type'] === 'application/json' && response.data.length > 0) {
                yield put(RECEIVED_NOTIFICATION(response.data));
            }

            if (response.headers['content-type'] === 'text/html') { // Happens in redirect to login page after logout
                loop = false;
            }

            yield call(delay, 60000);
        }
    } catch (err) {
        // Error will break the loop
        yield put(ERROR_RECEIVED_NOTIFICATION(err));
    }
}
