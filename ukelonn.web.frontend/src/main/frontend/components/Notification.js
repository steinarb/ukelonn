import { spawnNotification } from './spawnnotification.js';
import { useSelector, useDispatch } from 'react-redux';
import { UPDATE_NOTIFICATIONMESSAGE } from '../actiontypes';

export default function Notification() {
    const  notificationMessage = useSelector(state => state.notificationMessage);
    const dispatch = useDispatch();
    if (notificationMessage) {
        if (Notification) {
            spawnNotification(notificationMessage);
            dispatch(UPDATE_NOTIFICATIONMESSAGE(null));
        } else {
            console.log('Notification not supported by browser');
        }
    }

    return null;
}
