import React from 'react';
import { createRoot } from 'react-dom/client';
import App from './components/App';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { UPDATE_NOTIFICATIONAVAILABLE } from './actiontypes';
import rootReducer from './reducers';
const baseUrl = Array.from(document.scripts).map(s => s.src).filter(src => src.includes('assets/'))[0].replace(/\/assets\/.*/, '');
const basename = new URL(baseUrl).pathname;
import { api } from './api';
import listeners from './listeners';

const store = configureStore({
    reducer: rootReducer(basename),
    middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(api.middleware).prepend(listeners.middleware),
});

if (typeof Notification !== 'undefined') {
    Notification.requestPermission().then(function(result) {
        store.dispatch(UPDATE_NOTIFICATIONAVAILABLE(true));
        console.log(result);
    });
} else {
    store.dispatch(UPDATE_NOTIFICATIONAVAILABLE(false));
}

const container = document.getElementById('root');
const root = createRoot(container);

root.render(
    <Provider store={store}>
        <App history={history} basename={basename} />
    </Provider>,
);
