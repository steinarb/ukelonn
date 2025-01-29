import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostUserPasswordMutation,
} from '../api';
import { setPassword1, setPassword2 } from '../reducers/passwordSlice';
import { Link } from 'react-router';
import Locale from './Locale';
import Users from './Users';
import Logout from './Logout';

export default function AdminUsersChangePassword() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const user = useSelector(state => state.user);
    const password = useSelector(state => state.password);
    const dispatch = useDispatch();
    const [ postUserPassword ] = usePostUserPasswordMutation();
    const onChangePasswordClicked = async () => await postUserPassword({ user, ...password });

    return (
        <div>
            <nav>
                <Link to="/admin/users">
                    &lt;-
                    &nbsp;
                    {text.administrateUsers}
                </Link>
                <h1>{text.changeUsersPassword}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="users">{text.chooseUser}</label>
                        <div>
                            <Users id="users" />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="password1">{text.password}:</label>
                        <div>
                            <input
                                id="password1"
                                type="password"
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
                                onChange={e => dispatch(setPassword2(e.target.value))} />
                            { password.passwordsNotIdentical && <span>{text.passwordsAreNotIdentical}</span> }
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button
                                onClick={onChangePasswordClicked}>
                                {text.changePassword}
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
