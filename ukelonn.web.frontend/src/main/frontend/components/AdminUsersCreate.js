import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { isEmail } from 'validator';
import { userIsNotLoggedIn } from '../common/login';
import {
    CLEAR_USER_AND_PASSWORD,
    UPDATE_USER,
    UPDATE_PASSWORDS,
    CREATE_USER_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Users from './Users';
import Amount from './Amount';

class AdminUsersCreate extends Component {
    componentDidMount() {
        this.props.onClearUserAndPassword();
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
            onUsernameChange,
            onEmailChange,
            onFirstnameChange,
            onLastnameChange,
            onPassword1Change,
            onPassword2Change,
            onSaveCreatedUser,
            onLogout,
        } = this.props;

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
                    <input id="username" type="text" value={user.username} onChange={(event) => onUsernameChange(event.target.value)} />
                    { usernameEmpty && <span>Brukernavn kan ikke være tomt</span> }
                    { usernameExists && <span>Brukernavnet finnes fra før</span> }
                    <br/>
                    <label htmlFor="email">Epostadresse</label>
                    <input id="email" type="text" value={user.email} onChange={(event) => onEmailChange(event.target.value)} />
                    { user.email && !isEmail(user.email) && <span>Ikke en gyldig epostadresse</span> }
                    <br/>
                    <label htmlFor="firstname">Fornavn</label>
                    <input id="firstname" type="text" value={user.firstname} onChange={(event) => onFirstnameChange(event.target.value)} />
                    <br/>
                    <label htmlFor="lastname">Etternavn</label>
                    <input id="lastname" type="text" value={user.lastname} onChange={(event) => onLastnameChange(event.target.value)} />
                    <br/>
                    <label htmlFor="password1">Passord:</label>
                    <input id="password1" type='password' value={passwords.password1} onChange={(event) => onPassword1Change(event.target.value)} />
                    <br/>
                    <label htmlFor="password2">Gjenta passord:</label>
                    <input id="password2" type='password' value={passwords.password2} onChange={(event) => onPassword2Change(event.target.value)}/>
                    { passwords.passwordsNotIdentical && <span>Passordene er ikke identiske</span> }
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

function mapStateToProps(state) {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        user: state.user,
        passwords: state.passwords,
        usernames: state.usernames,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onClearUserAndPassword: () => {
            dispatch(CLEAR_USER_AND_PASSWORD());
        },
        onUsernameChange: (username) => dispatch(UPDATE_USER({ username })),
        onEmailChange: (email) => dispatch(UPDATE_USER({ email })),
        onFirstnameChange: (firstname) => dispatch(UPDATE_USER({ firstname })),
        onLastnameChange: (lastname) => dispatch(UPDATE_USER({ lastname })),
        onPassword1Change: (password1) => dispatch(UPDATE_PASSWORDS({ password1 })),
        onPassword2Change: (password2) => dispatch(UPDATE_PASSWORDS({ password2 })),
        onSaveCreatedUser: (user, passwords) => dispatch(CREATE_USER_REQUEST({ user, passwords })),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

AdminUsersCreate = connect(mapStateToProps, mapDispatchToProps)(AdminUsersCreate);

export default AdminUsersCreate;
