import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import {
    MODIFY_PASSWORD1,
    MODIFY_PASSWORD2,
    CHANGE_PASSWORD_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import Users from './Users';
import Logout from './Logout';

function AdminUsersChangePassword(props) {
    const {
        text,
        password1,
        password2,
        passwordsNotIdentical,
    } = props;
    const dispatch = useDispatch();

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/users">
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
                            <input id="password1" type='password' value={password1} onChange={e => dispatch(MODIFY_PASSWORD1(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="password2">{text.repeatPassword}:</label>
                        <div>
                            <input id="password2" type='password' value={password2} onChange={e => dispatch(MODIFY_PASSWORD2(e.target.value))} />
                            { passwordsNotIdentical && <span>{text.passwordsAreNotIdentical}</span> }
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => dispatch(CHANGE_PASSWORD_BUTTON_CLICKED())}>{text.changePassword}</button>
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

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        password1: state.password1,
        password2: state.password2,
        passwordsNotIdentical: state.passwordsNotIdentical,
    };
}

export default connect(mapStateToProps)(AdminUsersChangePassword);
