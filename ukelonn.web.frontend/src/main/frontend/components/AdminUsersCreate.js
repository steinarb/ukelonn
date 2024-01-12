import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import { isEmail } from 'validator';
import {
    MODIFY_USER_USERNAME,
    MODIFY_USER_EMAIL,
    MODIFY_USER_FIRSTNAME,
    MODIFY_USER_LASTNAME,
    MODIFY_PASSWORD1,
    MODIFY_PASSWORD2,
    MODIFY_USER_IS_ADMINISTRATOR,
    CREATE_USER_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import Logout from './Logout';

export default function AdminUsersCreate() {
    const text = useSelector(state => state.displayTexts);
    const usernames = useSelector(state => state.usernames);
    const userUsername = useSelector(state => state.userUsername);
    const userEmail = useSelector(state => state.userEmail);
    const userFirstname = useSelector(state => state.userFirstname);
    const userLastname = useSelector(state => state.userLastname);
    const userIsAdministrator = useSelector(state => state.userIsAdministrator);
    const password1 = useSelector(state => state.password1);
    const password2 = useSelector(state => state.password2);
    const passwordsNotIdentical = useSelector(state => state.passwordsNotIdentical);
    const dispatch = useDispatch();

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
                    <div className="form-group row mb-2">
                        <label htmlFor="username" className="col-form-label col-5">{text.username}</label>
                        <div className="col-7">
                            <input
                                id="username"
                                className={usernameInputClass}
                                type="text"
                                value={userUsername}
                                onChange={e => dispatch(MODIFY_USER_USERNAME(e.target.value))} />
                            { usernameEmpty && <span className="invalid-feedback d-block">{text.usernameCanNotBeEmpty}</span> }
                            { usernameExists && <span className="invalid-feedback d-block">{text.usernameExists}</span> }
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="email" className="col-form-label col-5">{text.emailAddress}</label>
                        <div className="col-7">
                            <input
                                id="email"
                                className={emailInputClass}
                                type="text"
                                value={userEmail}
                                onChange={e => dispatch(MODIFY_USER_EMAIL(e.target.value))} />
                            { userEmail && !isEmail(userEmail) && <span className="invalid-feedback d-block">{text.notAValidEmailAddress}</span> }
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="firstname" className="col-form-label col-5">{text.firstName}</label>
                        <div className="col-7">
                            <input
                                id="firstname"
                                className="form-control"
                                type="text"
                                value={userFirstname}
                                onChange={e => dispatch(MODIFY_USER_FIRSTNAME(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="lastname" className="col-form-label col-5">{text.lastName}</label>
                        <div className="col-7">
                            <input
                                id="lastname"
                                className="form-control"
                                type="text"
                                value={userLastname}
                                onChange={e => dispatch(MODIFY_USER_LASTNAME(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="password1" className="col-form-label col-5">{text.password}:</label>
                        <div className="col-7">
                            <input
                                id="password1"
                                className={passwordGroupClass}
                                type='password'
                                value={password1}
                                onChange={e => dispatch(MODIFY_PASSWORD1(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="password2" className="col-form-label col-5">{text.repeatPassword}:</label>
                        <div className="col-7">
                            <input
                                id="password2"
                                className={passwordGroupClass}
                                type="password"
                                value={password2}
                                onChange={e => dispatch(MODIFY_PASSWORD2(e.target.value))}/>
                            { passwordsNotIdentical && <span className="invalid-feedback d-block">{text.passwordsAreNotIdentical}</span> }
                        </div>
                    </div>
                    <div className="row">
                        <div className="col">
                            <div className="form-check">
                                <input
                                    id="administrator"
                                    className="form-control"
                                    type="checkbox"
                                    checked={userIsAdministrator}
                                    onChange={e => dispatch(MODIFY_USER_IS_ADMINISTRATOR(e.target.checked))} />
                                <label htmlFor="administrator" className="form-check-label">{text.administrator}</label>
                            </div>
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button
                                className="btn btn-primary"
                                onClick={() => dispatch(CREATE_USER_BUTTON_CLICKED())}>
                                {text.createUser}
                            </button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <Logout />
        </div>
    );
}
