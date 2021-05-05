import { spawnNotification } from './spawnnotification.js';
import { connect } from 'react-redux';
import {
    UPDATE_NOTIFICATIONMESSAGE,
} from '../actiontypes';

function Notification(props) {
    const { notificationMessage, onNullNotification } = props;
    if (notificationMessage) {
        if (Notification) {
            spawnNotification(notificationMessage);
            onNullNotification();
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

function mapDispatchToProps(dispatch) {
    return {
        onNullNotification: () => dispatch(UPDATE_NOTIFICATIONMESSAGE(null)),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Notification);
