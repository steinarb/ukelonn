import 'regenerator-runtime';
import React from 'react';
import { createRoot } from 'react-dom/client';
import App from './components/App';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { UPDATE_NOTIFICATIONAVAILABLE } from './actiontypes';
import { push } from 'redux-first-history';
import createUkelonnReducer from './reducers';
const baseUrl = Array.from(document.scripts).map(s => s.src).filter(src => src.includes('assets/'))[0].replace(/\/assets\/.*/, '');
const basename = new URL(baseUrl).pathname;
import { createBrowserHistory } from 'history';
import { createReduxHistoryContext } from "redux-first-history";
import { api } from './api';
import listeners from './listeners';

const {
  createReduxHistory,
  routerMiddleware,
  routerReducer
} = createReduxHistoryContext({ history: createBrowserHistory(), basename });
const store = configureStore({
    reducer: createUkelonnReducer(routerReducer, basename),
    middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(routerMiddleware).concat(api.middleware).prepend(listeners.middleware),
});
const history = createReduxHistory(store);
// Use redux to reload the current path to trigger the locationChange() saga
const router = store.getState().router;
const pathname = router.location.pathname;
store.dispatch(push(pathname));

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
