import { createListenerMiddleware } from '@reduxjs/toolkit';
import { LOCATION_CHANGE } from 'redux-first-history';
import { api } from './api';
import {
    SELECTED_PAYMENT_TYPE,
    MODIFY_PAYMENT_AMOUNT,
    SELECT_USER,
    MODIFY_USER_IS_ADMINISTRATOR,
    MODIFY_PASSWORDS_NOT_IDENTICAL,
    CLEAR_USER_AND_PASSWORDS,
    CLEAR_JOB_TYPE_FORM,
    CLEAR_JOB_TYPE_CREATE_FORM,
    CLEAR_PAYMENT_TYPE_FORM,
    CLEAR_BONUS,
} from './actiontypes';
import { isUsersLoaded, isPasswordModified, isRejectedRequest } from './matchers';

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
    actionCreator: SELECTED_PAYMENT_TYPE,
    effect: (action, listenerApi) => {
        const paymenttype = action.payload;
        if (paymenttype === -1) {
            const balance = listenerApi.getState().account.balance;
            listenerApi.dispatch(MODIFY_PAYMENT_AMOUNT(balance));
        }
        if (paymenttype && paymenttype.transactionAmount > 0) {
            listenerApi.dispatch(MODIFY_PAYMENT_AMOUNT(paymenttype.transactionAmount));
        } else {
            const balance = listenerApi.getState().account.balance;
            listenerApi.dispatch(MODIFY_PAYMENT_AMOUNT(balance));
        }
    }
})

// reload admin status from backend when user is changed
listeners.startListening({
    actionCreator: SELECT_USER,
    effect: async (action, listenerApi) => {
        listenerApi.dispatch(api.endpoints.postUserAdminstatus.initiate(action.payload, { forceRefetch: true }));
    }
})

listeners.startListening({
    matcher: isUsersLoaded,
    effect: (action, listenerApi) => {
        listenerApi.dispatch(CLEAR_USER_AND_PASSWORDS());
    }
})

listeners.startListening({
    matcher: isPasswordModified,
    effect: (action, listenerApi) => {
        const password1 = listenerApi.getState().password1;
        const password2 = listenerApi.getState().password2;
        if (!password2) {
            // if second password is empty we don't compare because it probably hasn't been typed into yet
            listenerApi.dispatch(MODIFY_PASSWORDS_NOT_IDENTICAL(false));
        } else {
            listenerApi.dispatch(MODIFY_PASSWORDS_NOT_IDENTICAL(password1 !== password2));
        }
    }
})

// Blank forms when navigating
listeners.startListening({
    type: LOCATION_CHANGE,
    effect: async (action, listenerApi) => {
        const { location = {} } = action.payload || {};
        const pathname = findPathname(location, listenerApi.getState().basename);

        if (pathname === '/admin/jobtypes/modify') {
            listenerApi.dispatch(CLEAR_JOB_TYPE_FORM());
        }

        if (pathname === '/admin/jobtypes/create') {
            listenerApi.dispatch(CLEAR_JOB_TYPE_CREATE_FORM());
        }

        if (pathname === '/admin/paymenttypes/modify' || pathname === '/admin/paymenttypes/create') {
            listenerApi.dispatch((CLEAR_PAYMENT_TYPE_FORM()));
        }

        if (pathname === '/admin/users/modify' || pathname === '/admin/users/password' || pathname === '/admin/users/create') {
            listenerApi.dispatch(CLEAR_USER_AND_PASSWORDS());
        }

        if (pathname === '/admin/users/create') {
            listenerApi.dispatch(api.endpoints.getUsers.initiate(undefined));
        }

        if (pathname === '/admin/bonuses/create' || pathname === '/admin/bonuses/modify' || pathname === '/admin/bonuses/delete') {
            listenerApi.dispatch(CLEAR_BONUS());
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
