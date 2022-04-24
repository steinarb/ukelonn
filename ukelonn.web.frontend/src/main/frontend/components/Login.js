import React, { useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Redirect } from 'react-router';
import { LOGIN_REQUEST } from '../actiontypes';
import LoginErrorMessage from './LoginErrorMessage';

export default function Login() {
    const loginResponse = useSelector(state => state.loginResponse);
    const dispatch = useDispatch();
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
                <button onClick={() => dispatch(LOGIN_REQUEST({ username, password: btoa(password) }))}>Login</button>
            </form>
            <LoginErrorMessage loginResponse={loginResponse} />
        </div>
    );
}
