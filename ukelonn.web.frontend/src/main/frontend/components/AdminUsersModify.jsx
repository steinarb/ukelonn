import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostUserModifyMutation,
    usePostUserChangeadminstatusMutation,
} from '../api';
import { setUsername, setEmail, setFirstname, setLastname } from '../reducers/userSlice';
import { MODIFY_USER_IS_ADMINISTRATOR } from '../actiontypes';
import { Link } from 'react-router';
import Locale from './Locale';
import Users from './Users';
import Logout from './Logout';

export default function AdminUsersModify() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const user = useSelector(state => state.user);
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
                                value={user.username}
                                onChange={e => dispatch(setUsername(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="email" className="col-form-label col-5">{text.emailAddress}</label>
                        <div className="col-7">
                            <input
                                id="email"
                                className="form-control"
                                type="text"
                                value={user.email}
                                onChange={e => dispatch(setEmail(e.target.value))} />
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
