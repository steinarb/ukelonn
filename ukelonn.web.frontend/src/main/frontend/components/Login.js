import React, { Component } from 'react';
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
                            <input id="username" type="text" name="username" className="stretch-to-fill" onChange={(event) => onUsernameChange(event.target.value)}></input>
                        </div>
                    </div>
                    <div className="mdl-grid graybox">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="password">Passord:</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <input id="password" type="password" name="password" className="stretch-to-fill" onChange={(event) => onPasswordChange(event.target.value)}/>
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
