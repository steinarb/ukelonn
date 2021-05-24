import 'regenerator-runtime';
import React from 'react';
import ReactDOM from 'react-dom';
import App from './components/App';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import createSagaMiddleware from 'redux-saga';
import {
    INITIAL_LOGIN_STATE_REQUEST,
    UPDATE_NOTIFICATIONAVAILABLE,
    DEFAULT_LOCALE_REQUEST,
    AVAILABLE_LOCALES_REQUEST,
} from './actiontypes';
import createUkelonnReducer from './reducers';
import { rootSaga } from './sagas';
const sagaMiddleware = createSagaMiddleware();
import { createBrowserHistory } from 'history';
import { routerMiddleware } from 'connected-react-router';

const history = createBrowserHistory();
const store = configureStore({
    reducer: createUkelonnReducer(history),
    middleware: [
        sagaMiddleware,
        routerMiddleware(history),
    ],
});
sagaMiddleware.run(rootSaga);
store.dispatch(INITIAL_LOGIN_STATE_REQUEST());
store.dispatch(DEFAULT_LOCALE_REQUEST());
store.dispatch(AVAILABLE_LOCALES_REQUEST());

if (typeof Notification !== 'undefined') {
    Notification.requestPermission().then(function(result) {
        store.dispatch(UPDATE_NOTIFICATIONAVAILABLE(true));
        console.log(result);
    });
} else {
    store.dispatch(UPDATE_NOTIFICATIONAVAILABLE(false));
}


ReactDOM.render(
    <Provider store={store}>
      <App history={history} />
    </Provider>,
    document.getElementById('root')
);
