import React from 'react';
import ReactDOM from 'react-dom';
import App from './components/App';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import createSagaMiddleware from 'redux-saga';
import {
    UPDATE,
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

if (typeof Notification !== 'undefined') {
    Notification.requestPermission().then(function(result) {
        store.dispatch(UPDATE({ notificationAvailable: true }));
        console.log(result);
    });
} else {
    store.dispatch(UPDATE({ notificationAvailable: false }));
}


ReactDOM.render(
    <Provider store={store}>
      <App history={history} />
    </Provider>,
    document.getElementById('root')
);
