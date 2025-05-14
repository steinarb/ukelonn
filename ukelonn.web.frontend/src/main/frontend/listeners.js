import { createListenerMiddleware } from '@reduxjs/toolkit';
import { LOCATION_CHANGE } from 'redux-first-history';
import { selectPaymentType, setAmount  } from './reducers/transactionSlice';
import { clearTransactionType } from './reducers/transactionTypeSlice';
import { selectUser, clearUser } from './reducers/userSlice';
import { clearPassword } from './reducers/passwordSlice';
import { clearBonus } from './reducers/bonusSlice';
import { api } from './api';
import { MODIFY_USER_IS_ADMINISTRATOR } from './actiontypes';
import { isUsersLoaded, isRejectedRequest } from './matchers';
import { spawnNotification } from './components/spawnnotification';

const listeners = createListenerMiddleware();

listeners.startListening({
    matcher: api.endpoints.postLogout.matchFulfilled,
    effect: (action, listenerApi) => {
        const basename = listenerApi.getState().basename;
        location.href = basename + '/'; // Go to top after logout
    }
})

// Set payment amount based on selected payment type
// must be in an RTK listener since it uses state.account.balance
// when payment type amount is empty
listeners.startListening({
    actionCreator: selectPaymentType,
    effect: (action, listenerApi) => {
        const paymenttype = action.payload;
        if (paymenttype === -1) {
            const balance = listenerApi.getState().account.balance;
            listenerApi.dispatch(setAmount(balance));
        }
        if (paymenttype && paymenttype.transactionAmount > 0) {
            listenerApi.dispatch(setAmount(paymenttype.transactionAmount));
        } else {
            const balance = listenerApi.getState().account.balance;
            listenerApi.dispatch(setAmount(balance));
        }
    }
})

// reload admin status from backend when user is changed
listeners.startListening({
    actionCreator: selectUser,
    effect: async (action, listenerApi) => {
        listenerApi.dispatch(api.endpoints.postUserAdminstatus.initiate(action.payload, { forceRefetch: true }));
    }
})

// Blank forms when navigating
listeners.startListening({
    type: LOCATION_CHANGE,
    effect: async (action, listenerApi) => {
        const { location = {} } = action.payload || {};
        const pathname = findPathname(location, listenerApi.getState().basename);

        if (pathname === '/admin/jobtypes/modify') {
            listenerApi.dispatch(clearTransactionType());
        }

        if (pathname === '/admin/jobtypes/create') {
            listenerApi.dispatch(clearTransactionType());
        }

        if (pathname === '/admin/paymenttypes/modify' || pathname === '/admin/paymenttypes/create') {
            listenerApi.dispatch((clearTransactionType()));
        }

        if (pathname === '/admin/users/modify' || pathname === '/admin/users/password' || pathname === '/admin/users/create') {
            listenerApi.dispatch(clearUser());
            listenerApi.dispatch(clearPassword());
        }

        if (pathname === '/admin/users/create') {
            listenerApi.dispatch(api.endpoints.getUsers.initiate(undefined));
        }

        if (pathname === '/admin/bonuses/create' || pathname === '/admin/bonuses/modify' || pathname === '/admin/bonuses/delete') {
            listenerApi.dispatch(clearBonus());
        }
    }
});

// Display notification when receiving non-empty notification
listeners.startListening({
    matcher: api.endpoints.getNotification.matchFulfilled,
    effect: (action, listenerApi) => {
        const notifications = action.payload;
        if (notifications.length) {
            const notification = notifications[0];

            if (notification.message) {
                // Trigger reload of displayed balance after payment
                listenerApi.dispatch(api.util.invalidateTags(['PaymentMade']));
                if (Notification) {
                    spawnNotification(notification);
                } else {
                    console.log('Notification not supported by browser');
                }
            }
        }
    }
});

listeners.startListening({
    matcher: isRejectedRequest,
    effect: ({ payload }) => {
        const { originalStatus } = payload || {};
        const statusCode = parseInt(originalStatus);
        if (statusCode === 401 || statusCode === 403) {
            location.reload(true); // Will return to current location after the login process
        }
    }
})

listeners.startListening({
    matcher: api.endpoints.postLogout.matchFulfilled,
    effect: (action, listenerApi) => {
        if (!action.payload.suksess) {
            const basename = listenerApi.getState().basename;
            location.href = basename + '/'; // Setting app top location before going to login, to avoid ending up in "/unauthorized" after login
        }
    }
})

function findPathname(location, basename) {
    if (basename === '/') {
        return location.pathname;
    }

    return location.pathname.replace(new RegExp('^' + basename + '(.*)'), '$1');
}

export default listeners;
