import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import Users from './Users';
import Amount from './Amount';

class AdminUsersChangePassword extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onUserList();
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let {
            haveReceivedResponseFromLogin,
            loginResponse,
            users,
            usersMap,
            user,
            passwords,
            passwordsNotIdentical,
            onUsersFieldChange,
            onPasswordsFieldChange,
            onSaveUpdatedPassword,
            onLogout,
        } = this.state;

        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        return (
            <div>
                <h1>Bytt passord på bruker</h1>
                <br/>
                <Link to="/ukelonn/admin/users">Administer brukere</Link>
                <br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="users">Velg bruker</label>
                    <Users id="users" users={users} usersMap={usersMap} value={user.fullname} onUsersFieldChange={onUsersFieldChange} />
                    <br/>
                    <label htmlFor="password1">Passord:</label>
                    <input id="password1" type='password' value={passwords.password1} onChange={(event) => onPasswordsFieldChange({ password1: event.target.value }, passwords)} />
                    <br/>
                    <label htmlFor="password2">Gjenta passord:</label>
                    <input id="password2" type='password' value={passwords.password2} onChange={(event) => onPasswordsFieldChange({ password2: event.target.value }, passwords)} />
                    { passwordsNotIdentical && <span>Passordene er ikke identiske</span> }
                    <br/>
                    <button onClick={() => onSaveUpdatedPassword(user, passwords)}>Endre passord</button>
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
        users: state.users,
        usersMap: new Map(state.users.map(i => [i.fullname, i])),
        user: state.user,
        passwords: state.passwords,
        passwordsNotIdentical: state.passwordsNotIdentical,
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
        onUserList: () => dispatch({ type: 'USERS_REQUEST' }),
        onUsersFieldChange: (selectedValue, usersMap) => {
            let user = usersMap.get(selectedValue);
            let changedField = {
                user: {...user},
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onPasswordsFieldChange: (formValue, passwordsFromState) => {
            const passwords = { ...passwordsFromState, ...formValue };
            const passwordsNotIdentical = checkIfPasswordsAreNotIdentical(passwords);
            let changedField = {
                passwords,
                passwordsNotIdentical,
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onSaveUpdatedPassword: (user, passwords) => dispatch({ type: 'MODIFY_USER_PASSWORD_REQUEST', user, passwords }),
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
    };
};

AdminUsersChangePassword = connect(mapStateToProps, mapDispatchToProps)(AdminUsersChangePassword);

export default AdminUsersChangePassword;
