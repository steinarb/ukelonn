import React from 'react';
import ReactDOM from 'react-dom';
import App from './components/App';
import { applyMiddleware, createStore } from 'redux';
import { composeWithDevTools } from 'redux-devtools-extension';
import { Provider } from 'react-redux';
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
const store = createStore(ukelonnReducer,
                          composeWithDevTools(
                              applyMiddleware(
                                  sagaMiddleware,
                                  routerMiddleware(history)
                              )
                          )
                         );
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
        <App />
    </Provider>,
    document.getElementById('root')
);
