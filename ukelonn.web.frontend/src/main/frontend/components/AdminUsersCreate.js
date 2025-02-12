import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostUserCreateMutation,
    usePostUserChangeadminstatusMutation,
} from '../api';
import { setUsername, setEmail, setFirstname, setLastname } from '../reducers/userSlice';
import { setPassword1, setPassword2 } from '../reducers/passwordSlice';
import { MODIFY_USER_IS_ADMINISTRATOR } from '../actiontypes';
import { Link } from 'react-router';
import { isEmail } from 'validator';
import Locale from './Locale';
import Logout from './Logout';

export default function AdminUsersCreate() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const usernames = useSelector(state => state.usernames);
    const user = useSelector(state => state.user);
    const userIsAdministrator = useSelector(state => state.userIsAdministrator);
    const password = useSelector(state => state.password);
    const userAndPasswords = { user, ...password };
    const dispatch = useDispatch();
    const [ postUserCreate ] = usePostUserCreateMutation();
    const [ postUserChangeadminstatus ] = usePostUserChangeadminstatusMutation();
    const onCreateUserClicked = async () => {
        const { data: updatedUsers } = await postUserCreate(userAndPasswords);
        const createdUser = updatedUsers.find(u => u.username = username) || {};
        await postUserChangeadminstatus({ administrator, user: createdUser });
    };

    const usernameEmpty = !user.username;
    const usernameExists = usernames.indexOf(user.username) > -1;
    const emailIsNotValid = user.email && !isEmail(user.email);
    const usernameInputClass = 'form-control' + (usernameEmpty || usernameExists ? ' is-invalid' : '');
    const emailInputClass = 'form-control' + (emailIsNotValid ? ' is-invalid' : '');
    const passwordGroupClass = 'form-control' + (password.passwordsNotIdentical ? ' is-invalid' : '');

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/admin/users">
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
                                value={user.username}
                                onChange={e => dispatch(setUsername(e.target.value))} />
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
                                value={user.email}
                                onChange={e => dispatch(setEmail(e.target.value))} />
                            { emailIsNotValid && <span className="invalid-feedback d-block">{text.notAValidEmailAddress}</span> }
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="firstname" className="col-form-label col-5">{text.firstName}</label>
                        <div className="col-7">
                            <input
                                id="firstname"
                                className="form-control"
                                type="text"
                                value={user.firstname}
                                onChange={e => dispatch(setFirstname(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="lastname" className="col-form-label col-5">{text.lastName}</label>
                        <div className="col-7">
                            <input
                                id="lastname"
                                className="form-control"
                                type="text"
                                value={user.lastname}
                                onChange={e => dispatch(setLastname(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="password1" className="col-form-label col-5">{text.password}:</label>
                        <div className="col-7">
                            <input
                                id="password1"
                                className={passwordGroupClass}
                                type='password'
                                value={password.password1}
                                onChange={e => dispatch(setPassword1(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="password2" className="col-form-label col-5">{text.repeatPassword}:</label>
                        <div className="col-7">
                            <input
                                id="password2"
                                className={passwordGroupClass}
                                type="password"
                                value={password.password2}
                                onChange={e => dispatch(setPassword2(e.target.value))}/>
                            { password.passwordsNotIdentical && <span className="invalid-feedback d-block">{text.passwordsAreNotIdentical}</span> }
                        </div>
                    </div>
                    <div className="row">
                        <div className="col">
                            <div className="form-check">
                                <input
                                    id="administrator"
                                    className="form-check-input"
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
                                onClick={onCreateUserClicked}>
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
