import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import LoginErrorMessage from './LoginErrorMessage';

let Login = ({username, password, loginResponse, onFieldChange, onLogin}) => {
    if (loginResponse.roles.length > 0) {
        if (loginResponse.roles[0] === 'ukelonnadmin') {
            return (<Redirect to="/ukelonn/admin" />);
        }

        return (<Redirect to="/ukelonn/user" />);
    }

    return (
        <div className="Login">
            <h1>Ukelønn login</h1>
            <form  onSubmit={ e => { e.preventDefault(); }}>
                <label htmlFor="username">Brukernavn:</label>
                <input id="username" type='text' name='username' onChange={(event) => onFieldChange({ username: event.target.value })}></input><br/>
                <label htmlFor="password">Passord:</label>
                <input id="password" type='password' name='password' onChange={(event) => onFieldChange({ password: event.target.value })}/><br/>
                <button onClick={() => onLogin(username, password)}>Login</button>
            </form>
            <LoginErrorMessage loginResponse={loginResponse} />
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
