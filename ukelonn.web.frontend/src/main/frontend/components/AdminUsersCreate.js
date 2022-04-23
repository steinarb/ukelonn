import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import { isEmail } from 'validator';
import {
    MODIFY_USER_USERNAME,
    MODIFY_USER_EMAIL,
    MODIFY_USER_FIRSTNAME,
    MODIFY_USER_LASTNAME,
    MODIFY_PASSWORD1,
    MODIFY_PASSWORD2,
    MODIFY_USER_IS_ADMINISTRATOR,
    CREATE_USER_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import Logout from './Logout';

function AdminUsersCreate(props) {
    const {
        text,
        usernames,
        userUsername,
        userEmail,
        userFirstname,
        userLastname,
        userIsAdministrator,
        password1,
        password2,
        passwordsNotIdentical,
    } = props;
    const dispatch = useDispatch();

    const usernameEmpty = !userUsername;
    const usernameExists = usernames.indexOf(userUsername) > -1;

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/users">
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
                            <input id="username" type="text" value={userUsername} onChange={e => dispatch(MODIFY_USER_USERNAME(e.target.value))} />
                            { usernameEmpty && <span>{text.usernameCanNotBeEmpty}</span> }
                            { usernameExists && <span>{text.usernameExists}</span> }
                        </div>
                    </div>
                    <div>
                        <label htmlFor="email">{text.emailAddress}</label>
                        <div>
                            <input id="email" type="text" value={userEmail} onChange={e => dispatch(MODIFY_USER_EMAIL(e.target.value))} />
                            { userEmail && !isEmail(userEmail) && <span>{text.notAValidEmailAddress}</span> }
                        </div>
                    </div>
                    <div>
                        <label htmlFor="firstname">{text.firstName}</label>
                        <div>
                            <input id="firstname" type="text" value={userFirstname} onChange={e => dispatch(MODIFY_USER_FIRSTNAME(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="lastname">{text.lastName}</label>
                        <div>
                            <input id="lastname" type="text" value={userLastname} onChange={e => dispatch(MODIFY_USER_LASTNAME(e.target.value))} />
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
                            <input id="password2" type="password" value={password2} onChange={e => dispatch(MODIFY_PASSWORD2(e.target.value))}/>
                            { passwordsNotIdentical && <span>{text.passwordsAreNotIdentical}</span> }
                        </div>
                    </div>
                    <div>
                        <label htmlFor="administrator">{text.administrator}</label>
                        <div>
                            <input id="administrator" type="checkbox" checked={userIsAdministrator} onChange={e => dispatch(MODIFY_USER_IS_ADMINISTRATOR(e.target.checked))} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => dispatch(CREATE_USER_BUTTON_CLICKED())}>{text.createUser}</button>
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
        usernames: state.usernames,
        userUsername: state.userUsername,
        userEmail: state.userEmail,
        userFirstname: state.userFirstname,
        userLastname: state.userLastname,
        userIsAdministrator: state.userIsAdministrator,
        password1: state.password1,
        password2: state.password2,
        passwordsNotIdentical: state.passwordsNotIdentical,
    };
}

export default connect(mapStateToProps)(AdminUsersCreate);
