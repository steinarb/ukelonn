import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostUserPasswordMutation,
} from '../api';
import { Link } from 'react-router';
import { MODIFY_PASSWORD1, MODIFY_PASSWORD2 } from '../actiontypes';
import Locale from './Locale';
import Users from './Users';
import Logout from './Logout';

export default function AdminUsersChangePassword() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const userid = useSelector(state => state.userid);
    const username = useSelector(state => state.userUsername);
    const email = useSelector(state => state.userEmail);
    const firstname = useSelector(state => state.userFirstname);
    const lastname = useSelector(state => state.userLastname);
    const user = {userid, username, email, firstname, lastname };
    const password1 = useSelector(state => state.password1);
    const password2 = useSelector(state => state.password2);
    const passwordsNotIdentical = useSelector(state => state.passwordsNotIdentical);
    const dispatch = useDispatch();
    const [ postUserPassword ] = usePostUserPasswordMutation();
    const onChangePasswordClicked = async () => await postUserPassword({ user, password1, password2 });

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
                                onChange={e => dispatch(MODIFY_PASSWORD2(e.target.value))} />
                            { passwordsNotIdentical && <span>{text.passwordsAreNotIdentical}</span> }
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
