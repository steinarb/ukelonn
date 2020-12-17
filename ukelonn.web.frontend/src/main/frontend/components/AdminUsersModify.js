import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    UPDATE_USER,
    UPDATE_USER_IS_ADMINISTRATOR,
    REQUEST_ADMIN_STATUS,
    MODIFY_USER_REQUEST,
    CHANGE_ADMIN_STATUS,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Users from './Users';
import Amount from './Amount';

function AdminUsersModify(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let {
        text,
        user,
        userIsAdministrator,
        users,
        onUsersFieldChange,
        onUsernameChange,
        onEmailChange,
        onFirstnameChange,
        onLastnameChange,
        onUpdateUserIsAdministrator,
        onSaveUpdatedUser,
        onLogout,
    } = props;

    return (
        <div>
            <Link to="/ukelonn/admin/users">
                &lt;-
                &nbsp;
                {text.administrateUsers}
            </Link>
            <header>
                <div>
                    <h1>{text.modifyUsers}</h1>
                </div>
            </header>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="users">{text.chooseUser}</label>
                        <div>
                            <Users id="users" value={user.userid} users={users} onUsersFieldChange={onUsersFieldChange} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="username">{text.username}</label>
                        <div>
                            <input id="username" type="text" value={user.username} onChange={(event) => onUsernameChange(event.target.value)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="email">{text.emailAddress}</label>
                        <div>
                            <input id="email" type="text" value={user.email} onChange={(event) => onEmailChange(event.target.value)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="firstname">{text.firstName}</label>
                        <div>
                            <input id="firstname" type="text" value={user.firstname} onChange={(event) => onFirstnameChange(event.target.value)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="lastname">{text.lastName}</label>
                        <div>
                            <input id="lastname" type="text" value={user.lastname} onChange={(event) => onLastnameChange(event.target.value)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="administrator">{text.administrator}</label>
                        <div>
                            <input id="administrator" type="checkbox" checked={userIsAdministrator} onChange={e => onUpdateUserIsAdministrator(e)} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => onSaveUpdatedUser(user, userIsAdministrator)}>{text.saveUserModifications}</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        user: state.user,
        userIsAdministrator: state.userIsAdministrator,
        users: state.users,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onUsersFieldChange: (selectedValue, users) => {
            const selectedValueInt = parseInt(selectedValue, 10);
            let user = users.find(u => u.userid === selectedValueInt);
            dispatch(UPDATE_USER({ ...user }));
            dispatch(REQUEST_ADMIN_STATUS({ username: user.username }));
        },
        onUsernameChange: (username) => dispatch(UPDATE_USER({ username })),
        onEmailChange: (email) => dispatch(UPDATE_USER({ email })),
        onFirstnameChange: (firstname) => dispatch(UPDATE_USER({ firstname })),
        onLastnameChange: (lastname) => dispatch(UPDATE_USER({ lastname })),
        onUpdateUserIsAdministrator: e => dispatch(UPDATE_USER_IS_ADMINISTRATOR(e.target.checked)),
        onSaveUpdatedUser: (user, administrator) => {
            const { userid, username, email, firstname, lastname } = user;
            dispatch(MODIFY_USER_REQUEST({ userid, username, email, firstname, lastname }));
            dispatch(CHANGE_ADMIN_STATUS({ user: { username }, administrator }));
        },
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminUsersModify);
