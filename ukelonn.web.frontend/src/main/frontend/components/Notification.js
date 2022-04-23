import { spawnNotification } from './spawnnotification.js';
import { connect, useDispatch } from 'react-redux';
import {
    UPDATE_NOTIFICATIONMESSAGE,
} from '../actiontypes';

function Notification(props) {
    const { notificationMessage } = props;
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

function mapStateToProps(state) {
    return {
        notificationMessage: state.notificationMessage,
    };
}

export default connect(mapStateToProps)(Notification);
