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
                            <input id="username" className="form-control" type="text" name="username" onChange={e => setUsername(e.target.value)}></input><br/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password" className="col-form-label col-3 mr-2">Passord:</label>
                        <div className="col-8">
                            <input id="password" className="form-control" type="password" name='password' onChange={e => setPassword(e.target.value)}/><br/>
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
        loginResponse: state.loginResponse
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogin: (username, password) => dispatch(LOGIN_REQUEST({ username, password })),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Login);
