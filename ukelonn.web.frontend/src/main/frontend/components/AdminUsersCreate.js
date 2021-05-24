import React from 'react';
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
import Locale from './Locale';

function AdminUsersCreate(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let {
        text,
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
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/admin/users">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateUsers}
                </Link>
                <h1>{text.addUser}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="username" className="col-form-label col-5">{text.username}</label>
                        <div className="col-7">
                            <input id="username" className={usernameInputClass} type="text" value={user.username} onChange={(event) => onUsernameChange(event.target.value)} />
                            { usernameEmpty && <span className="invalid-feedback d-block">{text.usernameCanNotBeEmpty}</span> }
                            { usernameExists && <span className="invalid-feedback d-block">{text.usernameExists}</span> }
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="email" className="col-form-label col-5">{text.emailAddress}</label>
                        <div className="col-7">
                            <input id="email" className={emailInputClass} type="text" value={user.email} onChange={(event) => onEmailChange(event.target.value)} />
                            { emailIsNotValid && <span className="invalid-feedback d-block">{text.notAValidEmailAddress}</span> }
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="firstname" className="col-form-label col-5">{text.firstName}</label>
                        <div className="col-7">
                            <input id="firstname" className="form-control" type="text" value={user.firstname} onChange={(event) => onFirstnameChange(event.target.value)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="lastname" className="col-form-label col-5">{text.lastName}</label>
                        <div className="col-7">
                            <input id="lastname" className="form-control" type="text" value={user.lastname} onChange={(event) => onLastnameChange(event.target.value)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password1" className="col-form-label col-5">{text.password}:</label>
                        <div className="col-7">
                            <input id="password1" className={passwordGroupClass} type='password' value={passwords.password1} onChange={(event) => onPassword1Change(event.target.value)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password2" className="col-form-label col-5">{text.repeatPassword}:</label>
                        <div className="col-7">
                            <input id="password2" className={passwordGroupClass} type='password' value={passwords.password2} onChange={(event) => onPassword2Change(event.target.value)}/>
                            { passwords.passwordsNotIdentical && <span className="invalid-feedback d-block">{text.passwordsAreNotIdentical}</span> }
                        </div>
                    </div>
                    <div clasName="row">
                        <div className="col">
                            <div className="form-check">
                                <input id="administrator" className="form-check-input" type="checkbox" checked={userIsAdministrator} onChange={e => onUpdateUserIsAdministrator(e)} />
                                <label htmlFor="administrator" className="form-check-label">{text.administrator}</label>
                            </div>
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => onSaveCreatedUser(user, passwords, userIsAdministrator)}>{text.createUser}</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>{text.returnToTop}</button>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
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
