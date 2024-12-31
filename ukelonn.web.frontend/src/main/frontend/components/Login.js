import React, { useState } from 'react';
import { useGetLoginQuery, usePostLoginMutation } from '../api';
import { Navigate } from 'react-router';
import LoginErrorMessage from './LoginErrorMessage';

export default function Login() {
    const { data: loginResponse = { roles: [] } } = useGetLoginQuery();
    const [ username, setUsername ] = useState('');
    const [ password, setPassword ] = useState('');
    const [ postLogin ] = usePostLoginMutation();
    const onLoginClicked = async () => { await postLogin({ username, password: btoa(password) }) };

    if (loginResponse.roles.length > 0) {
        if (loginResponse.roles[0] === 'ukelonnadmin') {
            return (<Navigate to="/admin" />);
        }

        return (<Navigate to="/user" />);
    }

    return (
        <div className="Login">
            <h1>Ukelønn login</h1>
            <form  onSubmit={ e => { e.preventDefault(); }}>
                <label htmlFor="username">Brukernavn:</label>
                <input id="username" type="text" name="username" autoComplete="username" onChange={e => setUsername(e.target.value)}></input><br/>
                <label htmlFor="password">Passord:</label>
                <input id="password" type="password" name="password" autoComplete="current-password" onChange={e => setPassword(e.target.value)}/><br/>
                <button onClick={onLoginClicked}>Login</button>
            </form>
            <LoginErrorMessage loginResponse={loginResponse} />
        </div>
    );
}
