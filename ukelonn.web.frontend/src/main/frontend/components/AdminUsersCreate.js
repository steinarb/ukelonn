import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostUserCreateMutation,
    usePostUserChangeadminstatusMutation,
} from '../api';
import { Link } from 'react-router';
import { isEmail } from 'validator';
import {
    MODIFY_USER_USERNAME,
    MODIFY_USER_EMAIL,
    MODIFY_USER_FIRSTNAME,
    MODIFY_USER_LASTNAME,
    MODIFY_PASSWORD1,
    MODIFY_PASSWORD2,
    MODIFY_USER_IS_ADMINISTRATOR,
} from '../actiontypes';
import Locale from './Locale';
import Logout from './Logout';

export default function AdminUsersCreate() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const usernames = useSelector(state => state.usernames);
    const username = useSelector(state => state.userUsername);
    const email = useSelector(state => state.userEmail);
    const firstname = useSelector(state => state.userFirstname);
    const lastname = useSelector(state => state.userLastname);
    const user = { username, email, firstname, lastname };
    const userIsAdministrator = useSelector(state => state.userIsAdministrator);
    const password1 = useSelector(state => state.password1);
    const password2 = useSelector(state => state.password2);
    const passwordsNotIdentical = useSelector(state => state.passwordsNotIdentical);
    const userAndPasswords = { user, password1, password2, passwordsNotIdentical };
    const dispatch = useDispatch();
    const [ postUserCreate ] = usePostUserCreateMutation();
    const [ postUserChangeadminstatus ] = usePostUserChangeadminstatusMutation();
    const onCreateUserClicked = async () => {
        const { data: updatedUsers } = await postUserCreate(userAndPasswords);
        const createdUser = updatedUsers.find(u => u.username = username) || {};
        await postUserChangeadminstatus({ administrator, user: createdUser });
    };

    const usernameEmpty = !username;
    const usernameExists = usernames.indexOf(username) > -1;

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
                                value={username}
                                onChange={e => dispatch(MODIFY_USER_USERNAME(e.target.value))} />
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
                                value={email}
                                onChange={e => dispatch(MODIFY_USER_EMAIL(e.target.value))} />
                            { email && !isEmail(email) && <span>{text.notAValidEmailAddress}</span> }
                        </div>
                    </div>
                    <div>
                        <label htmlFor="firstname">{text.firstName}</label>
                        <div>
                            <input
                                id="firstname"
                                type="text"
                                value={firstname}
                                onChange={e => dispatch(MODIFY_USER_FIRSTNAME(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="lastname">{text.lastName}</label>
                        <div>
                            <input
                                id="lastname"
                                type="text"
                                value={lastname}
                                onChange={e => dispatch(MODIFY_USER_LASTNAME(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="password1">{text.password}:</label>
                        <div>
                            <input
                                id="password1"
                                type='password'
                                value={password1}
                                onChange={e => dispatch(MODIFY_PASSWORD1(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="password2">{text.repeatPassword}:</label>
                        <div>
                            <input
                                id="password2"
                                type="password"
                                value={password2}
                                onChange={e => dispatch(MODIFY_PASSWORD2(e.target.value))}/>
                            { passwordsNotIdentical && <span>{text.passwordsAreNotIdentical}</span> }
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
