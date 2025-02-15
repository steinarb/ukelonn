import { spawnNotification } from './spawnnotification.js';
import { useDispatch } from 'react-redux';
import { useGetLoginQuery, useGetNotificationQuery, api } from '../api';

export default function Notification() {
    const { data: loginResponse = {}, isSuccess: loginIsSuccess } = useGetLoginQuery();
    const { data: notifications, isSuccess: notificationIsSuccess } = useGetNotificationQuery(loginResponse.username, {
        skip: !loginIsSuccess,
        pollingInterval: 60000,
    });
    const dispatch = useDispatch();

    if (notificationIsSuccess && notifications.length) {
        const notification = notifications[0];

        if (notification.message) {
            // Update the balance after payment
            dispatch(api.endpoints.getAccount.initiate(loginResponse.username));
            if (Notification) {
                spawnNotification(notification);
            } else {
                console.log('Notification not supported by browser');
            }
        }
    }

    return null;
}
