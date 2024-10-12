import 'regenerator-runtime';
import React from 'react';
import { createRoot } from 'react-dom/client';
import axios from 'axios';
import App from './components/App';
import { configureStore, Tuple } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import createSagaMiddleware from 'redux-saga';
import {
    INITIAL_LOGIN_STATE_REQUEST,
    UPDATE_NOTIFICATIONAVAILABLE,
    DEFAULT_LOCALE_REQUEST,
    AVAILABLE_LOCALES_REQUEST,
} from './actiontypes';
import { push } from 'redux-first-history';
import createUkelonnReducer from './reducers';
import { rootSaga } from './sagas';
const baseUrl = Array.from(document.scripts).map(s => s.src).filter(src => src.includes('assets/'))[0].replace(/\/assets\/.*/, '');
const basename = new URL(baseUrl).pathname;
axios.defaults.baseURL = baseUrl;
const sagaMiddleware = createSagaMiddleware();
import { createBrowserHistory } from 'history';
import { createReduxHistoryContext } from "redux-first-history";

const {
  createReduxHistory,
  routerMiddleware,
  routerReducer
} = createReduxHistoryContext({ history: createBrowserHistory(), basename });
const store = configureStore({
    reducer: createUkelonnReducer(routerReducer),
    middleware: () => new Tuple(sagaMiddleware, routerMiddleware),
});
const history = createReduxHistory(store);
sagaMiddleware.run(rootSaga);
store.dispatch(INITIAL_LOGIN_STATE_REQUEST());
store.dispatch(DEFAULT_LOCALE_REQUEST());
store.dispatch(AVAILABLE_LOCALES_REQUEST());
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
