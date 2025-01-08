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
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/admin/users">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateUsers}
                </Link>
                <h1>{text.modifyUsers}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row mb-2">
                        <label htmlFor="users" className="col-form-label col-5">{text.chooseUser}</label>
                        <div className="col-7">
                            <Users id="users" className="form-control" />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="username" className="col-form-label col-5">{text.username}</label>
                        <div className="col-7">
                            <input
                                id="username"
                                className="form-control"
                                type="text"
                                value={username}
                                onChange={e => dispatch(MODIFY_USER_USERNAME(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="email" className="col-form-label col-5">{text.emailAddress}</label>
                        <div className="col-7">
                            <input
                                id="email"
                                className="form-control"
                                type="text"
                                value={email}
                                onChange={e => dispatch(MODIFY_USER_EMAIL(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="firstname" className="col-form-label col-5">{text.firstName}</label>
                        <div className="col-7">
                            <input
                                id="firstname"
                                className="form-control"
                                type="text"
                                value={firstname}
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
                                value={lastname}
                                onChange={e => dispatch(MODIFY_USER_LASTNAME(e.target.value))} />
                        </div>
                    </div>
                    <div className="row">
                        <div className="col">
                            <div className="form-check">
                                <input
                                    id="administrator"
                                    className="form-check-input"
                                    type="checkbox"
                                    checked={administrator}
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
                                onClick={onSaveUserClicked}>
                                {text.saveUserModifications}
                            </button>
                        </div>
                    </div>
                </div>
                <br/>
            </form>
            <br/>
            <Logout />
        </div>
    );
}
