import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { isEmail } from 'validator';
import { userIsNotLoggedIn } from '../common/login';
import {
    CLEAR_USER_AND_PASSWORDS,
    MODIFY_USER_USERNAME,
    MODIFY_USER_EMAIL,
    MODIFY_USER_FIRSTNAME,
    MODIFY_USER_LASTNAME,
    MODIFY_PASSWORD1,
    MODIFY_PASSWORD2,
    MODIFY_USER_IS_ADMINISTRATOR,
    CREATE_USER_BUTTON_CLICKED,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';

function AdminUsersCreate(props) {
    const {
        text,
        usernames,
        userUsername,
        userEmail,
        userFirstname,
        userLastname,
        userIsAdministrator,
        password1,
        password2,
        passwordsNotIdentical,
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

    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    const usernameEmpty = !userUsername;
    const usernameExists = usernames.indexOf(userUsername) > -1;
    const emailIsNotValid = userEmail && !isEmail(userEmail);
    const usernameInputClass = 'form-control' + (usernameEmpty || usernameExists ? ' is-invalid' : '');
    const emailInputClass = 'form-control' + (emailIsNotValid ? ' is-invalid' : '');
    const passwordGroupClass = 'form-control' + (passwordsNotIdentical ? ' is-invalid' : '');

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
                            <input id="username" className={usernameInputClass} type="text" value={userUsername} onChange={onUsernameChange} />
                            { usernameEmpty && <span className="invalid-feedback d-block">{text.usernameCanNotBeEmpty}</span> }
                            { usernameExists && <span className="invalid-feedback d-block">{text.usernameExists}</span> }
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="email" className="col-form-label col-5">{text.emailAddress}</label>
                        <div className="col-7">
                            <input id="email" className={emailInputClass} type="text" value={userEmail} onChange={onEmailChange} />
                            { userEmail && !isEmail(userEmail) && <span className="invalid-feedback d-block">{text.notAValidEmailAddress}</span> }
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="firstname" className="col-form-label col-5">{text.firstName}</label>
                        <div className="col-7">
                            <input id="firstname" className="form-control" type="text" value={userFirstname} onChange={onFirstnameChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="lastname" className="col-form-label col-5">{text.lastName}</label>
                        <div className="col-7">
                            <input id="lastname" className="form-control" type="text" value={userLastname} onChange={onLastnameChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password1" className="col-form-label col-5">{text.password}:</label>
                        <div className="col-7">
                            <input id="password1" className={passwordGroupClass} type='password' value={password1} onChange={onPassword1Change} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password2" className="col-form-label col-5">{text.repeatPassword}:</label>
                        <div className="col-7">
                            <input id="password2" className={passwordGroupClass} type="password" value={password2} onChange={onPassword2Change}/>
                            { passwordsNotIdentical && <span className="invalid-feedback d-block">{text.passwordsAreNotIdentical}</span> }
                        </div>
                    </div>
                    <div clasName="row">
                        <div className="col">
                            <div className="form-check">
                                <input id="administrator" className="form-check-input" type="checkbox" checked={userIsAdministrator} onChange={onUpdateUserIsAdministrator} />
                                <label htmlFor="administrator" className="form-check-label">{text.administrator}</label>
                            </div>
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => onSaveCreatedUser()}>{text.createUser}</button>
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
        usernames: state.usernames,
        userUsername: state.userUsername,
        userEmail: state.userEmail,
        userFirstname: state.userFirstname,
        userLastname: state.userLastname,
        userIsAdministrator: state.userIsAdministrator,
        password1: state.password1,
        password2: state.password2,
        passwordsNotIdentical: state.passwordsNotIdentical,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onClearUserAndPassword: () => dispatch(CLEAR_USER_AND_PASSWORDS()),
        onUsernameChange: e => dispatch(MODIFY_USER_USERNAME(e.target.value)),
        onEmailChange: e => dispatch(MODIFY_USER_EMAIL(e.target.value)),
        onFirstnameChange: e => dispatch(MODIFY_USER_FIRSTNAME(e.target.value)),
        onLastnameChange: e => dispatch(MODIFY_USER_LASTNAME(e.target.value)),
        onPassword1Change: e => dispatch(MODIFY_PASSWORD1(e.target.value)),
        onPassword2Change: e => dispatch(MODIFY_PASSWORD2(e.target.value)),
        onUpdateUserIsAdministrator: e => dispatch(MODIFY_USER_IS_ADMINISTRATOR(e.target.checked)),
        onSaveCreatedUser: () => dispatch(CREATE_USER_BUTTON_CLICKED()),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminUsersCreate);
