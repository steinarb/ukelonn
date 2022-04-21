import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { isEmail } from 'validator';
import {
    CLEAR_USER_AND_PASSWORDS,
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
        onUsernameChange,
        onEmailChange,
        onFirstnameChange,
        onLastnameChange,
        onPassword1Change,
        onPassword2Change,
        onUpdateUserIsAdministrator,
        onSaveCreatedUser,
    } = props;

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
                            <input id="username" type="text" value={userUsername} onChange={onUsernameChange} />
                            { usernameEmpty && <span>{text.usernameCanNotBeEmpty}</span> }
                            { usernameExists && <span>{text.usernameExists}</span> }
                        </div>
                    </div>
                    <div>
                        <label htmlFor="email">{text.emailAddress}</label>
                        <div>
                            <input id="email" type="text" value={userEmail} onChange={onEmailChange} />
                            { userEmail && !isEmail(userEmail) && <span>{text.notAValidEmailAddress}</span> }
                        </div>
                    </div>
                    <div>
                        <label htmlFor="firstname">{text.firstName}</label>
                        <div>
                            <input id="firstname" type="text" value={userFirstname} onChange={onFirstnameChange} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="lastname">{text.lastName}</label>
                        <div>
                            <input id="lastname" type="text" value={userLastname} onChange={onLastnameChange} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="password1">{text.password}:</label>
                        <div>
                            <input id="password1" type='password' value={password1} onChange={onPassword1Change} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="password2">{text.repeatPassword}:</label>
                        <div>
                            <input id="password2" type="password" value={password2} onChange={onPassword2Change}/>
                            { passwordsNotIdentical && <span>{text.passwordsAreNotIdentical}</span> }
                        </div>
                    </div>
                    <div>
                        <label htmlFor="administrator">{text.administrator}</label>
                        <div>
                            <input id="administrator" type="checkbox" checked={userIsAdministrator} onChange={onUpdateUserIsAdministrator} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => onSaveCreatedUser()}>{text.createUser}</button>
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

function mapDispatchToProps(dispatch) {
    return {
        onClearUserAndPassword: () => dispatch(CLEAR_USER_AND_PASSWORDS()),
        onUsernameChange: e => dispatch(MODIFY_USER_USERNAME(e.target.value)),
        onEmailChange: e => dispatch(MODIFY_USER_EMAIL(e.target.value)),
        onFirstnameChange: e => dispatch(MODIFY_USER_FIRSTNAME(e.target.value)),
        onLastnameChange: e => dispatch(MODIFY_USER_LASTNAME(e.target.value)),
        onPassword1Change: e => dispatch(MODIFY_PASSWORD1(e.target.value)),
        onPassword2Change: e => dispatch(MODIFY_PASSWORD2(e.target.value)),
        onUpdateUserIsAdministrator: e => dispatch(MODIFY_USER_IS_ADMINISTRATOR(e.target.checked)),
        onSaveCreatedUser: () => dispatch(CREATE_USER_BUTTON_CLICKED()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminUsersCreate);
