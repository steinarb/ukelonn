import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import {
    UPDATE_USERNAME,
    UPDATE_PASSWORD,
    LOGIN_REQUEST,
} from '../actiontypes';
import LoginErrorMessage from './LoginErrorMessage';

function Login(props) {
    const { username, password, loginResponse, onUsernameChange, onPasswordChange, onLogin } = props;
    if (loginResponse.roles.length > 0) {
        if (loginResponse.roles[0] === 'ukelonnadmin') {
            return (<Redirect to="/ukelonn/admin" />);
        }

        return (<Redirect to="/ukelonn/user" />);
    }

    return (
        <div className="Login">
            <header>
                <div className="pb-2 mt-4 mb-2 border-bottom bg-light">
                    <h1>Ukelønn login</h1>
                </div>
            </header>
            <div className="container">
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="form-group row">
                        <label htmlFor="username" className="col-form-label col-3 mr-2">Brukernavn:</label>
                        <div className="col-8">
                            <input id="username" className="form-control" type='text' name='username' onChange={(event) => onUsernameChange(event.target.value)}></input>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password" className="col-form-label col-3 mr-2">Passord:</label>
                        <div className="col-8">
                            <input id="password" className="form-control" type='password' name='password' onChange={(event) => onPasswordChange(event.target.value)}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="offset-xs-3 col-xs-9">
                            <button className="btn btn-primary" onClick={() => onLogin(username, password)}>Login</button>
                        </div>
                    </div>
                </form>
                <div className="row">
                    <LoginErrorMessage loginResponse={loginResponse} />
                </div>
            </div>

        </div>
    );
}

function mapStateToProps(state) {
    return {
        username: state.username,
        password: state.password,
        loginResponse: state.loginResponse
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onUsernameChange: username => dispatch(UPDATE_USERNAME(username)),
        onPasswordChange: password => dispatch(UPDATE_PASSWORD(password)),
        onLogin: (username, password) => dispatch(LOGIN_REQUEST({ username, password })),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Login);
