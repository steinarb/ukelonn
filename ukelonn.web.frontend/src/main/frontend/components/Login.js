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
        <div className="Login mdl-layout mdl-layout--fixed-header">
            <header className="mdl-layout__header">
                <div className="mdl-layout__header-row">
                    <span className="mdl-layout-title">Ukelønn login</span>
                    <div className="mdl-layout-spacer"></div>
                </div>
            </header>
            <main className="mdl-layout__content">
                <form  onSubmit={ e => { e.preventDefault(); }}>
                    <div className="mdl-grid graybox">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="username">Brukernavn:</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <input type='text' name='username' className='stretch-to-fill' onChange={(event) => onFieldChange({ username: event.target.value })}></input>
                        </div>
                    </div>
                    <div className="mdl-grid graybox">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="password">Passord:</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <input type='password' name='password' className='stretch-to-fill' onChange={(event) => onFieldChange({ password: event.target.value })}/>
                        </div>
                    </div>
                    <div className="mdl-grid">
                        <div className="mdl-cell mdl-cell--hide-phone mdl-cell--4-col-tablet mdl-cell--8-col-desktop">
                            &nbsp;
                        </div>
                        <div className="mdl-cell mdl-cell--4-col">
                            <button className="mdl-button mdl-js-button mdl-button--raised stretch-to-fill" onClick={() => onLogin(username, password)}>Login</button>
                        </div>
                    </div>
                </form>
                <LoginErrorMessage loginResponse={loginResponse} />
            </main>
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
