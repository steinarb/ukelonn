import React from 'react';
import ReactDOM from 'react-dom';
import App from "./components/App";
import { applyMiddleware, createStore, compose } from 'redux';
import createSagaMiddleware from 'redux-saga';
import { ukelonnReducer } from './reducers';
import { rootSaga } from './sagas';
const sagaMiddleware = createSagaMiddleware();
import { createBrowserHistory } from 'history';
import { routerMiddleware } from 'connected-react-router';

const history = createBrowserHistory();
const store = createStore(ukelonnReducer, compose(applyMiddleware(sagaMiddleware, routerMiddleware(history)), window.devToolsExtension ? window.devToolsExtension() : f => f));
sagaMiddleware.run(rootSaga);

ReactDOM.render(
    <App store={store} />,
    document.getElementById('root')
);
