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
    const passwordInputClass = 'form-control' + (password.passwordsNotIdentical ? ' is-invalid' : '');

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/admin/users">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateUsers}
                </Link>
                <h1>{text.changeUsersPassword}</h1>
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
                        <label htmlFor="password1" className="col-form-label col-5">{text.password}:</label>
                        <div className="col-7">
                            <input
                                id="password1"
                                className="form-control"
                                type="password"
                                value={password.password1}
                                onChange={e => dispatch(setPassword1(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="password2" className="col-form-label col-5">{text.repeatPassword}:</label>
                        <div className="col-7">
                            <input
                                id="password2"
                                className={passwordInputClass}
                                type="password"
                                value={password.password2}
                                onChange={e => dispatch(setPassword2(e.target.value))} />
                            { password.passwordsNotIdentical && <span className="invalid-feedback d-block">{text.passwordsAreNotIdentical}</span> }
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button
                                className="btn btn-primary"
                                onClick={onChangePasswordClicked}>
                                {text.changePassword}
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
