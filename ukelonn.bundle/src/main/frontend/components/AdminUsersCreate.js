import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import Users from './Users';
import Amount from './Amount';

class AdminUsersCreate extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onClearUserAndPassword();
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let {
            haveReceivedResponseFromLogin,
            loginResponse,
            user,
            passwords,
            onUserFieldChange,
            onPasswordsFieldChange,
            onSaveCreatedUser,
            onLogout,
        } = this.state;

        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        return (
            <div>
                <h1>Legg til ny bruker</h1>
                <br/>
                <Link to="/ukelonn/admin/users">Administer brukere</Link>
                <br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="username">Brukernavn</label>
                    <input id="username" type="text" value={user.username} onChange={(event) => onUserFieldChange({username: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="email">Epostadresse</label>
                    <input id="email" type="text" value={user.email} onChange={(event) => onUserFieldChange({email: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="firstname">Fornavn</label>
                    <input id="firstname" type="text" value={user.firstname} onChange={(event) => onUserFieldChange({firstname: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="lastname">Etternavn</label>
                    <input id="lastname" type="text" value={user.lastname} onChange={(event) => onUserFieldChange({lastname: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="password">Passord:</label>
                    <input id="password" type='password' value={passwords.password} onChange={(event) => onPasswordsFieldChange({ password: event.target.value }, passwords)} />
                    <br/>
                    <label htmlFor="password2">Gjenta passord:</label>
                    <input id="password2" type='password' value={passwords.password2} onChange={(event) => onPasswordsFieldChange({ password2: event.target.value }, passwords)} />
                    <br/>
                    <button onClick={() => onSaveCreatedUser(user, passwords)}>Lag bruker</button>
                </form>
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
            </div>
        );
    };
};

const mapStateToProps = state => {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        user: state.user,
        passwords: state.passwords,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onClearUserAndPassword: () => {
            dispatch({ type: 'CLEAR_USER_AND_PASSWORD' });
        },
        onUserFieldChange: (formValue, user) => {
            let changedField = {
                user: { ...user, ...formValue }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onPasswordsFieldChange: (formValue, passwords) => {
            let changedField = {
                passwords: { ...passwords, ...formValue }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onSaveCreatedUser: (user, passwords) => dispatch({ type: 'CREATE_USER_REQUEST', user, passwords }),
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
    };
};

AdminUsersCreate = connect(mapStateToProps, mapDispatchToProps)(AdminUsersCreate);

export default AdminUsersCreate;
