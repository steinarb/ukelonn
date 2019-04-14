import React from 'react';
import ReactDOM from 'react-dom';
import App from "./components/App";
import { applyMiddleware, createStore, compose } from 'redux';
import createSagaMiddleware from 'redux-saga';
import {
    UPDATE,
} from './actiontypes';
import ukelonnReducer from './reducers';
import { rootSaga } from './sagas';
const sagaMiddleware = createSagaMiddleware();
import { createBrowserHistory } from 'history';
import { routerMiddleware } from 'connected-react-router';

const history = createBrowserHistory();
const store = createStore(ukelonnReducer, compose(applyMiddleware(sagaMiddleware, routerMiddleware(history)), window.devToolsExtension ? window.devToolsExtension() : f => f));
sagaMiddleware.run(rootSaga);

if (typeof Notification !== "undefined") {
    Notification.requestPermission().then(function(result) {
        store.dispatch(UPDATE({ notificationAvailable: true }));
        console.log(result);
    });
} else {
    store.dispatch(UPDATE({ notificationAvailable: false }));
}


ReactDOM.render(
    <App store={store} />,
    document.getElementById('root')
);
