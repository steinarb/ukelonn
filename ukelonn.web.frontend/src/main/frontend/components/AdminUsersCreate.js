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

    return (
        <div>
            <nav>
                <Link to="/admin/users">
                    &lt;-
                    &nbsp;
                    {text.administrateUsers}
                </Link>
                <h1>{text.addUser}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="username">{text.username}</label>
                        <div>
                            <input
                                id="username"
                                type="text"
                                value={user.username}
                                onChange={e => dispatch(setUsername(e.target.value))} />
                            { usernameEmpty && <span>{text.usernameCanNotBeEmpty}</span> }
                            { usernameExists && <span>{text.usernameExists}</span> }
                        </div>
                    </div>
                    <div>
                        <label htmlFor="email">{text.emailAddress}</label>
                        <div>
                            <input
                                id="email"
                                type="text"
                                value={user.email}
                                onChange={e => dispatch(setEmail(e.target.value))} />
                            { user.email && !isEmail(user.email) && <span>{text.notAValidEmailAddress}</span> }
                        </div>
                    </div>
                    <div>
                        <label htmlFor="firstname">{text.firstName}</label>
                        <div>
                            <input
                                id="firstname"
                                type="text"
                                value={user.firstname}
                                onChange={e => dispatch(setFirstname(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="lastname">{text.lastName}</label>
                        <div>
                            <input
                                id="lastname"
                                type="text"
                                value={user.lastname}
                                onChange={e => dispatch(setLastname(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="password1">{text.password}:</label>
                        <div>
                            <input
                                id="password1"
                                type='password'
                                value={password.password1}
                                onChange={e => dispatch(setPassword1(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="password2">{text.repeatPassword}:</label>
                        <div>
                            <input
                                id="password2"
                                type="password"
                                value={password.password2}
                                onChange={e => dispatch(setPassword2(e.target.value))}/>
                            { password.passwordsNotIdentical && <span>{text.passwordsAreNotIdentical}</span> }
                        </div>
                    </div>
                    <div>
                        <label htmlFor="administrator">{text.administrator}</label>
                        <div>
                            <input
                                id="administrator"
                                type="checkbox"
                                checked={userIsAdministrator}
                                onChange={e => dispatch(MODIFY_USER_IS_ADMINISTRATOR(e.target.checked))} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button
                                onClick={onCreateUserClicked}>
                                {text.createUser}
                            </button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <Logout />
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}
