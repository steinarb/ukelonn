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
import Locale from './Locale';
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
            <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/admin/users">
                <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                &nbsp;
                {text.administrateUsers}
            </Link>
            <header>
                <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                    <h1>{text.modifyUsers}</h1>
                </div>
                <div>
                    <Locale />
                </div>
            </header>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="users" className="col-form-label col-5">{text.chooseUser}</label>
                        <div className="col-7">
                            <Users id="users" className="form-control" value={user.userid} users={users} onUsersFieldChange={onUsersFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="username" className="col-form-label col-5">{text.username}</label>
                        <div className="col-7">
                            <input id="username" className="form-control" type="text" value={user.username} onChange={(event) => onUsernameChange(event.target.value)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="email" className="col-form-label col-5">{text.emailAddress}</label>
                        <div className="col-7">
                            <input id="email" className="form-control" type="text" value={user.email} onChange={(event) => onEmailChange(event.target.value)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="firstname" className="col-form-label col-5">{text.firstName}</label>
                        <div className="col-7">
                            <input id="firstname" className="form-control" type="text" value={user.firstname} onChange={(event) => onFirstnameChange(event.target.value)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="lastname" className="col-form-label col-5">{text.lastName}</label>
                        <div className="col-7">
                            <input id="lastname" className="form-control" type="text" value={user.lastname} onChange={(event) => onLastnameChange(event.target.value)} />
                        </div>
                    </div>
                    <div clasName="row">
                        <div className="col">
                            <div className="form-check">
                                <input id="administrator" className="form-check-input" type="checkbox" checked={userIsAdministrator} onChange={e => onUpdateUserIsAdministrator(e)} />
                                <label htmlFor="administrator" className="form-check-label">{text.administrator}</label>
                            </div>
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => onSaveUpdatedUser(user, userIsAdministrator)}>{text.saveUserModifications}</button>
                        </div>
                    </div>
                </div>
                <br/>
            </form>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>{text.logout}</button>
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
