import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { isEmail } from 'validator';
import { userIsNotLoggedIn } from '../common/login';
import {
    CLEAR_USER_AND_PASSWORD,
    USERS_REQUEST,
    UPDATE,
    CREATE_USER_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Users from './Users';
import Amount from './Amount';

class AdminUsersCreate extends Component {
    componentDidMount() {
        this.props.onClearUserAndPassword();
        this.props.onUserList();
    }

    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let {
            usernames,
            user,
            passwords,
            passwordsNotIdentical,
            onUserFieldChange,
            onPasswordsFieldChange,
            onSaveCreatedUser,
            onLogout,
        } = this.props;

        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        const usernameEmpty = !user.username;
        const usernameExists = usernames.indexOf(user.username) > -1;

        return (
            <div>
                <h1>Legg til ny bruker</h1>
                <br/>
                <Link to="/ukelonn/admin/users">Administer brukere</Link>
                <br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="username">Brukernavn</label>
                    <input id="username" type="text" value={user.username} onChange={(event) => onUserFieldChange({username: event.target.value}, user)} />
                    { usernameEmpty && <span>Brukernavn kan ikke være tomt</span> }
                    { usernameExists && <span>Brukernavnet finnes fra før</span> }
                    <br/>
                    <label htmlFor="email">Epostadresse</label>
                    <input id="email" type="text" value={user.email} onChange={(event) => onUserFieldChange({email: event.target.value}, user)} />
                    { user.email && !isEmail(user.email) && <span>Ikke en gyldig epostadresse</span> }
                    <br/>
                    <label htmlFor="firstname">Fornavn</label>
                    <input id="firstname" type="text" value={user.firstname} onChange={(event) => onUserFieldChange({firstname: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="lastname">Etternavn</label>
                    <input id="lastname" type="text" value={user.lastname} onChange={(event) => onUserFieldChange({lastname: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="password1">Passord:</label>
                    <input id="password1" type='password' value={passwords.password1} onChange={(event) => onPasswordsFieldChange({ password1: event.target.value }, passwords)} />
                    <br/>
                    <label htmlFor="password2">Gjenta passord:</label>
                    <input id="password2" type='password' value={passwords.password2} onChange={(event) => onPasswordsFieldChange({ password2: event.target.value }, passwords)}/>
                    { passwordsNotIdentical && <span>Passordene er ikke identiske</span> }
                    <br/>
                    <button onClick={() => onSaveCreatedUser(user, passwords)}>Lag bruker</button>
                </form>
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
                <br/>
                <a href="../../../..">Tilbake til topp</a>
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
        passwordsNotIdentical: state.passwordsNotIdentical,
        usernames: state.usernames,
    };
};

const checkIfPasswordsAreNotIdentical = (passwords) => {
    let { password1, password2 } = passwords;
    if (!password2) {
        return false; // if second password is empty we don't compare because it probably hasn't been typed into yet
    }

    return password1 !== password2;
};

const mapDispatchToProps = dispatch => {
    return {
        onClearUserAndPassword: () => {
            dispatch(CLEAR_USER_AND_PASSWORD());
        },
        onUserList: () => dispatch(USERS_REQUEST()),
        onUserFieldChange: (formValue, user) => {
            let changedField = {
                user: { ...user, ...formValue }
            };
            dispatch(UPDATE(changedField));
        },
        onPasswordsFieldChange: (formValue, passwordsFromState) => {
            const passwords = { ...passwordsFromState, ...formValue };
            const passwordsNotIdentical = checkIfPasswordsAreNotIdentical(passwords);
            let changedField = {
                passwords,
                passwordsNotIdentical,
            };
            dispatch(UPDATE(changedField));
        },
        onSaveCreatedUser: (user, passwords) => dispatch(CREATE_USER_REQUEST({ user, passwords })),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
};

AdminUsersCreate = connect(mapStateToProps, mapDispatchToProps)(AdminUsersCreate);

export default AdminUsersCreate;
