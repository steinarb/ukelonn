import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import LoginErrorMessage from './LoginErrorMessage';

let Login = ({username, password, loginResponse, onFieldChange, onLogin}) => {
    if (loginResponse.roles.length > 0) {
        if (loginResponse.roles[0] === 'administrator') {
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
                            <input id="username" className="form-control" type='text' name='username' onChange={(event) => onFieldChange({ username: event.target.value })}></input>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password" className="col-form-label col-3 mr-2">Passord:</label>
                        <div className="col-8">
                            <input id="password" className="form-control" type='password' name='password' onChange={(event) => onFieldChange({ password: event.target.value })}/>
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
};

const mapStateToProps = state => {
    return {
        username: state.username,
        password: state.password,
        loginResponse: state.loginResponse
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onFieldChange: (changedField) => dispatch({ type: 'UPDATE', data: changedField }),
        onLogin: (username, password) => dispatch({ type: 'LOGIN_REQUEST', username, password }),
    };
};

Login = connect(mapStateToProps, mapDispatchToProps)(Login);

export default Login;
