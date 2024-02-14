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
                                onClick={() => dispatch(CHANGE_PASSWORD_BUTTON_CLICKED())}>
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
