import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import {
    MODIFY_PASSWORD1,
    MODIFY_PASSWORD2,
    CHANGE_PASSWORD_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import Users from './Users';
import Logout from './Logout';

export default function AdminUsersChangePassword() {
    const text = useSelector(state => state.displayTexts);
    const password1 = useSelector(state => state.password1);
    const password2 = useSelector(state => state.password2);
    const passwordsNotIdentical = useSelector(state => state.passwordsNotIdentical);
    const dispatch = useDispatch();
    const passwordInputClass = 'form-control' + (passwordsNotIdentical ? ' is-invalid' : '');

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/admin/users">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateUsers}
                </Link>
                <h1>{text.changeUsersPassword}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="users" className="col-form-label col-5">{text.chooseUser}</label>
                        <div className="col-7">
                            <Users id="users" className="form-control" />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password1" className="col-form-label col-5">{text.password}:</label>
                        <div className="col-7">
                            <input
                                id="password1"
                                className="form-control"
                                type="password"
                                value={password1}
                                onChange={e => dispatch(MODIFY_PASSWORD1(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password2" className="col-form-label col-5">{text.repeatPassword}:</label>
                        <div className="col-7">
                            <input
                                id="password2"
                                className={passwordInputClass}
                                type="password"
                                value={password2}
                                onChange={e => dispatch(MODIFY_PASSWORD2(e.target.value))} />
                            { passwordsNotIdentical && <span className="invalid-feedback d-block">{text.passwordsAreNotIdentical}</span> }
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button
                                className="btn btn-primary"
                                onClick={() => dispatch(CHANGE_PASSWORD_BUTTON_CLICKED())}>
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
