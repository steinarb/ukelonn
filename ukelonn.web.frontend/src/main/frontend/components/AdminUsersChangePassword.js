import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    USERS_REQUEST,
    UPDATE,
    MODIFY_USER_PASSWORD_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Users from './Users';
import Amount from './Amount';

class AdminUsersChangePassword extends Component {
    componentDidMount() {
        this.props.onUserList();
    }

    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let {
            users,
            usersMap,
            user,
            passwords,
            passwordsNotIdentical,
            onUsersFieldChange,
            onPasswordsFieldChange,
            onSaveUpdatedPassword,
            onLogout,
        } = this.props;


        const passwordInputClass = 'form-control' + (passwordsNotIdentical ? ' is-invalid' : '');

        return (
            <div>
                <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/admin/users">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    Administer brukere
                </Link>
                <header>
                    <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                        <h1>Bytt passord på bruker</h1>
                    </div>
                </header>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="container">
                        <div className="form-group row">
                            <label htmlFor="users" className="col-form-label col-5">Velg bruker</label>
                            <div className="col-7">
                                <Users id="users" className="form-control" users={users} usersMap={usersMap} value={user.fullname} onUsersFieldChange={onUsersFieldChange} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="password1" className="col-form-label col-5">Passord:</label>
                            <div className="col-7">
                                <input id="password1" className="form-control" type='password' value={passwords.password1} onChange={(event) => onPasswordsFieldChange({ password1: event.target.value }, passwords)} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="password2" className="col-form-label col-5">Gjenta passord:</label>
                            <div className="col-7">
                                <input id="password2" className={passwordInputClass} type='password' value={passwords.password2} onChange={(event) => onPasswordsFieldChange({ password2: event.target.value }, passwords)} />
                                { passwordsNotIdentical && <span className="invalid-feedback d-block">Passordene er ikke identiske</span> }
                            </div>
                        </div>
                        <div className="form-group row">
                            <div className="col-5"/>
                            <div className="col-7">
                                <button className="btn btn-primary" onClick={() => onSaveUpdatedPassword(user, passwords)}>Endre passord</button>
                            </div>
                        </div>
                    </div>
                </form>
                <br/>
                <button className="btn btn-default" onClick={() => onLogout()}>Logout</button>
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
        onUserList: () => dispatch(USERS_REQUEST()),
        onUsersFieldChange: (selectedValue, usersMap) => {
            let user = usersMap.get(selectedValue);
            let changedField = {
                user: {...user},
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
        onSaveUpdatedPassword: (user, passwords) => dispatch(MODIFY_USER_PASSWORD_REQUEST({ user, passwords })),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
};

AdminUsersChangePassword = connect(mapStateToProps, mapDispatchToProps)(AdminUsersChangePassword);

export default AdminUsersChangePassword;
