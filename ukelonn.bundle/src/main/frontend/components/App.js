import React, { Component } from 'react';
import { connect, Provider } from 'react-redux';
import { Switch, Route, BrowserRouter as Router, NavLink } from 'react-router-dom';
import Home from "./Home";
import Login from "./Login";
import User from "./User";
import Admin from "./Admin";
import { createBrowserHistory } from 'history';
import { applyMiddleware, createStore, compose } from 'redux';
import { connectRouter, routerMiddleware } from 'connected-react-router';
import createSagaMiddleware from 'redux-saga';
import { ukelonnReducer } from '../reducers';
import { rootSaga } from '../sagas';

const sagaMiddleware = createSagaMiddleware();
const history = createBrowserHistory();
const store = createStore(ukelonnReducer, compose(applyMiddleware(sagaMiddleware, routerMiddleware(history)), window.devToolsExtension ? window.devToolsExtension() : f => f));
sagaMiddleware.run(rootSaga);

class App extends Component {
    componentDidMount() {
        this.props.initialLoginStateRequest();
    }

    render() {
        return(
            <Provider store={store}>
                <Router>
                    <div className="App">
                        <Switch>
                            <Route exact path="/ukelonn/" component={Home} />
                            <Route path="/ukelonn/login" component={Login} />
                            <Route path="/ukelonn/user" component={User} />
                            <Route path="/ukelonn/admin" component={Admin} />
                        </Switch>
                    </div>
                </Router>
            </Provider>
        );
    }
}

// Dummy mapStateToProps, we don't need to map any state to props here, but we need a mapToProps function as the first argument of connect()
const mapStateToProps = (state) => ({});

const mapDispatchToProps = dispatch => {
    return {
        initialLoginStateRequest: () => dispatch({ type: 'INITIAL_LOGIN_STATE_REQUEST' })
    };
};

function connectWithStore(store, WrappedComponent, ...args) {
    var ConnectedWrappedComponent = connect(...args)(WrappedComponent);
    return function (props) {
        return <ConnectedWrappedComponent {...props} store={store} />;
    };
}

App = connectWithStore(store, App, mapStateToProps, mapDispatchToProps);

export default App;
