import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { isEmail } from 'validator';
import { userIsNotLoggedIn } from '../common/login';
import {
    CLEAR_USER_AND_PASSWORD,
    UPDATE_USER,
    UPDATE_USER_IS_ADMINISTRATOR,
    UPDATE_PASSWORDS,
    CREATE_USER_REQUEST,
    CHANGE_ADMIN_STATUS,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Users from './Users';
import Amount from './Amount';

function AdminUsersCreate(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let {
        usernames,
        user,
        userIsAdministrator,
        passwords,
        onUsernameChange,
        onEmailChange,
        onFirstnameChange,
        onLastnameChange,
        onPassword1Change,
        onPassword2Change,
        onUpdateUserIsAdministrator,
        onSaveCreatedUser,
        onLogout,
    } = props;

    const usernameEmpty = !user.username;
    const usernameExists = usernames.indexOf(user.username) > -1;
    const emailIsNotValid = user.email && !isEmail(user.email);
    const usernameInputClass = 'form-control' + (usernameEmpty || usernameExists ? ' is-invalid' : '');
    const emailInputClass = 'form-control' + (emailIsNotValid ? ' is-invalid' : '');
    const passwordGroupClass = 'form-control' + (passwords.passwordsNotIdentical ? ' is-invalid' : '');

    return (
        <div>
            <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/admin/users">
                <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                &nbsp;
                Administer brukere
            </Link>
            <header>
                <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                    <h1>Legg til ny bruker</h1>
                </div>
            </header>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="username" className="col-form-label col-5">Brukernavn</label>
                        <div className="col-7">
                            <input id="username" className={usernameInputClass} type="text" value={user.username} onChange={(event) => onUsernameChange(event.target.value)} />
                            { usernameEmpty && <span className="invalid-feedback d-block">Brukernavn kan ikke være tomt</span> }
                            { usernameExists && <span className="invalid-feedback d-block">Brukernavnet finnes fra før</span> }
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="email" className="col-form-label col-5">Epostadresse</label>
                        <div className="col-7">
                            <input id="email" className={emailInputClass} type="text" value={user.email} onChange={(event) => onEmailChange(event.target.value)} />
                            { emailIsNotValid && <span className="invalid-feedback d-block">Ikke en gyldig epostadresse</span> }
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="firstname" className="col-form-label col-5">Fornavn</label>
                        <div className="col-7">
                            <input id="firstname" className="form-control" type="text" value={user.firstname} onChange={(event) => onFirstnameChange(event.target.value)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="lastname" className="col-form-label col-5">Etternavn</label>
                        <div className="col-7">
                            <input id="lastname" className="form-control" type="text" value={user.lastname} onChange={(event) => onLastnameChange(event.target.value)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password1" className="col-form-label col-5">Passord:</label>
                        <div className="col-7">
                            <input id="password1" className={passwordGroupClass} type='password' value={passwords.password1} onChange={(event) => onPassword1Change(event.target.value)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password2" className="col-form-label col-5">Gjenta passord:</label>
                        <div className="col-7">
                            <input id="password2" className={passwordGroupClass} type='password' value={passwords.password2} onChange={(event) => onPassword2Change(event.target.value)}/>
                            { passwords.passwordsNotIdentical && <span className="invalid-feedback d-block">Passordene er ikke identiske</span> }
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="administrator" className="col-form-label col-5">Administrator</label>
                        <div className="col-1">
                            <input id="administrator" className="form-control" type="checkbox" checked={userIsAdministrator} onChange={e => onUpdateUserIsAdministrator(e)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => onSaveCreatedUser(user, passwords, userIsAdministrator)}>Lag bruker</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>Logout</button>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        user: state.user,
        userIsAdministrator: state.userIsAdministrator,
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
        onUpdateUserIsAdministrator: e => dispatch(UPDATE_USER_IS_ADMINISTRATOR(e.target.checked)),
        onSaveCreatedUser: (user, passwords, administrator) => {
            dispatch(CREATE_USER_REQUEST({ user, passwords }));
            dispatch(CHANGE_ADMIN_STATUS({ user: { username: user.username }, administrator }));
        },
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminUsersCreate);
