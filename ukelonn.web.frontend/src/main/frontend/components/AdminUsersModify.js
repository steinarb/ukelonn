import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostUserModifyMutation,
    usePostUserChangeadminstatusMutation,
} from '../api';
import { Link } from 'react-router';
import {
    MODIFY_USER_USERNAME,
    MODIFY_USER_EMAIL,
    MODIFY_USER_FIRSTNAME,
    MODIFY_USER_LASTNAME,
    MODIFY_USER_IS_ADMINISTRATOR,
} from '../actiontypes';
import Locale from './Locale';
import Users from './Users';
import Logout from './Logout';

export default function AdminUsersModify() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const userid = useSelector(state => state.userid);
    const username = useSelector(state => state.userUsername);
    const email = useSelector(state => state.userEmail);
    const firstname = useSelector(state => state.userFirstname);
    const lastname = useSelector(state => state.userLastname);
    const user = {userid, username, email, firstname, lastname };
    const administrator = useSelector(state => state.userIsAdministrator);
    const dispatch = useDispatch();
    const [ postUserModify ] =  usePostUserModifyMutation();
    const [ postUserChangeadminstatus ] = usePostUserChangeadminstatusMutation();
    const onSaveUserClicked = async () => { await postUserModify(user); await postUserChangeadminstatus({ administrator, user }); };

    return (
        <div>
            <nav>
                <Link to="/admin/users">
                    &lt;-
                    &nbsp;
                    {text.administrateUsers}
                </Link>
                <h1>{text.modifyUsers}</h1>
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
                        <label htmlFor="username">{text.username}</label>
                        <div>
                            <input
                                id="username"
                                type="text"
                                value={username}
                                onChange={e => dispatch(MODIFY_USER_USERNAME(e.target.value))} />
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
                        <label htmlFor="administrator">{text.administrator}</label>
                        <div>
                            <input
                                id="administrator"
                                type="checkbox"
                                checked={administrator}
                                onChange={e => dispatch(MODIFY_USER_IS_ADMINISTRATOR(e.target.checked))} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button
                                onClick={onSaveUserClicked}>
                                {text.saveUserModifications}
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
