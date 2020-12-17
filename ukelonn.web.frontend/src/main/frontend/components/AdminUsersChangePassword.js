import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    USERS_REQUEST,
    UPDATE_USER,
    UPDATE_PASSWORDS,
    MODIFY_USER_PASSWORD_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Users from './Users';
import Amount from './Amount';

function AdminUsersChangePassword(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let {
        text,
        users,
        user,
        passwords,
        onUsersFieldChange,
        onPassword1Change,
        onPassword2Change,
        onSaveUpdatedPassword,
        onLogout,
    } = props;

    return (
        <div>
            <h1>{text.changeUsersPassword}</h1>
            <br/>
            <Link to="/ukelonn/admin/users">{text.administrateUsers}</Link>
            <br/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <label htmlFor="users">{text.chooseUser}</label>
                <Users id="users" value={user.userid} users={users} onUsersFieldChange={onUsersFieldChange} />
                <br/>
                <label htmlFor="password1">{text.password}:</label>
                <input id="password1" type='password' value={passwords.password1} onChange={(event) => onPassword1Change(event.target.value)} />
                <br/>
                <label htmlFor="password2">{text.repeatPassword}:</label>
                <input id="password2" type='password' value={passwords.password2} onChange={(event) => onPassword2Change(event.target.value)} />
                { passwords.passwordsNotIdentical && <span>{text.passwordsAreNotIdentical}</span> }
                <br/>
                <button onClick={() => onSaveUpdatedPassword(user, passwords)}>{text.changePassword}</button>
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
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        users: state.users,
        user: state.user,
        passwords: state.passwords,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onUserList: () => dispatch(USERS_REQUEST()),
        onUsersFieldChange: (selectedValue, users) => {
            const selectedValueInt = parseInt(selectedValue, 10);
            let user = users.find(u => u.userid === selectedValueInt);
            dispatch(UPDATE_USER({ ...user }));
        },
        onPassword1Change: (password1) => dispatch(UPDATE_PASSWORDS({ password1 })),
        onPassword2Change: (password2) => dispatch(UPDATE_PASSWORDS({ password2 })),
        onSaveUpdatedPassword: (user, passwords) => dispatch(MODIFY_USER_PASSWORD_REQUEST({ user, passwords })),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminUsersChangePassword);
