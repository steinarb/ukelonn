import { useState } from 'react';
import { spawnNotification } from './spawnnotification.js';
import { useDispatch } from 'react-redux';
import { useGetLoginQuery, useGetNotificationQuery, api } from '../api';

export default function Notifier() {
    const [ cachedNotifications, setCachedNotifications ] = useState(null);
    const { data: loginResponse = {}, isSuccess: loginIsSuccess } = useGetLoginQuery();
    const { data: notifications } = useGetNotificationQuery(loginResponse.username, {
        skip: !loginIsSuccess,
        pollingInterval: 60000,
        selectFromResult: ({ data }) => ({ data }),
    });
    const dispatch = useDispatch();

    if (notifications && notifications !== cachedNotifications && notifications.length) {
        setCachedNotifications(notifications);
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
