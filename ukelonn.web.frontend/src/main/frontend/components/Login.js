import React, { useState } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { LOGIN_REQUEST } from '../actiontypes';
import LoginErrorMessage from './LoginErrorMessage';

function Login(props) {
    const {
        loginResponse,
        onLogin,
    } = props;
    const [ username, setUsername ] = useState('');
    const [ password, setPassword ] = useState('');
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
                <input id="username" type="text" name="username" onChange={e => setUsername(e.target.value)}></input><br/>
                <label htmlFor="password">Passord:</label>
                <input id="password" type="password" name='password' onChange={e => setPassword(e.target.value)}/><br/>
                <button onClick={() => onLogin(username, password)}>Login</button>
            </form>
            <LoginErrorMessage loginResponse={loginResponse} />
        </div>
    );
}

function mapStateToProps(state) {
    return {
        loginResponse: state.loginResponse
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogin: (username, password) => dispatch(LOGIN_REQUEST({ username, password })),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Login);
