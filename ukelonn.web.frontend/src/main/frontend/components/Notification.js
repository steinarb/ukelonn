import { spawnNotification } from './spawnnotification.js';
import { useDispatch } from 'react-redux';
import { useGetLoginQuery, useGetNotificationQuery, api } from '../api';

export default function Notification() {
    const { data: loginResponse = {}, isSuccess: loginIsSuccess } = useGetLoginQuery();
    const { data: notification = [] } = useGetNotificationQuery(loginResponse.username, {
        skip: !loginIsSuccess,
        pollingInterval: 60000,
    });
    const dispatch = useDispatch();

    if (notification.length) {
        const  notificationMessage = notification[0].message;

        if (notificationMessage) {
            // Update the balance after payment
            dispatch(api.endpoints.getAccount.initiate(loginResponse.username));
            if (Notification) {
                spawnNotification(notificationMessage);
            } else {
                console.log('Notification not supported by browser');
            }
        }
    }

    return null;
}
