import { spawnNotification } from './spawnnotification.js';
import React from 'react';
import { connect } from 'react-redux';
import {
    UPDATE,
} from '../actiontypes';

var Notification = ({notificationMessage, onNullNotification}) => {
    if (notificationMessage) {
        if (Notification) {
            spawnNotification(notificationMessage);
            onNullNotification();
        } else {
            console.log('Notification not supported by browser');
        }
    }

    return null;
};

const mapStateToProps = state => {
    return {
        notificationMessage: state.notificationMessage,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onNullNotification: () => dispatch(UPDATE({ notificationMessage: null })),
    };
};

Notification = connect(mapStateToProps, mapDispatchToProps)(Notification);

export default Notification;
