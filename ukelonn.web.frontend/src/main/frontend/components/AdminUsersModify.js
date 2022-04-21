import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import {
    MODIFY_USER_USERNAME,
    MODIFY_USER_EMAIL,
    MODIFY_USER_FIRSTNAME,
    MODIFY_USER_LASTNAME,
    MODIFY_USER_IS_ADMINISTRATOR,
    SAVE_USER_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import Users from './Users';
import Logout from './Logout';

function AdminUsersModify(props) {
    const {
        text,
        userUsername,
        userEmail,
        userFirstname,
        userLastname,
        userIsAdministrator,
        onUsernameChange,
        onEmailChange,
        onFirstnameChange,
        onLastnameChange,
        onUpdateUserIsAdministrator,
        onSaveUserButtonClicked,
    } = props;

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/users">
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
                            <input id="username" type="text" value={userUsername} onChange={onUsernameChange} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="email">{text.emailAddress}</label>
                        <div>
                            <input id="email" type="text" value={userEmail} onChange={onEmailChange} />
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
                        <label htmlFor="administrator">{text.administrator}</label>
                        <div>
                            <input id="administrator" type="checkbox" checked={userIsAdministrator} onChange={onUpdateUserIsAdministrator} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={onSaveUserButtonClicked}>{text.saveUserModifications}</button>
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
        userUsername: state.userUsername,
        userEmail: state.userEmail,
        userFirstname: state.userFirstname,
        userLastname: state.userLastname,
        userIsAdministrator: state.userIsAdministrator,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onUsernameChange: e => dispatch(MODIFY_USER_USERNAME(e.target.value)),
        onEmailChange: e => dispatch(MODIFY_USER_EMAIL(e.target.value)),
        onFirstnameChange: e => dispatch(MODIFY_USER_FIRSTNAME(e.target.value)),
        onLastnameChange: e => dispatch(MODIFY_USER_LASTNAME(e.target.value)),
        onUpdateUserIsAdministrator: e => dispatch(MODIFY_USER_IS_ADMINISTRATOR(e.target.checked)),
        onSaveUserButtonClicked: () => dispatch(SAVE_USER_BUTTON_CLICKED()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminUsersModify);
