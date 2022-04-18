import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    MODIFY_USER_USERNAME,
    MODIFY_USER_EMAIL,
    MODIFY_USER_FIRSTNAME,
    MODIFY_USER_LASTNAME,
    MODIFY_USER_IS_ADMINISTRATOR,
    SAVE_USER_BUTTON_CLICKED,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import Users from './Users';

function AdminUsersModify(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let {
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
        onLogout,
    } = props;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/admin/users">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateUsers}
                </Link>
                <h1>{text.modifyUsers}</h1>
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
                        <label htmlFor="username" className="col-form-label col-5">{text.username}</label>
                        <div className="col-7">
                            <input id="username" className="form-control" type="text" value={userUsername} onChange={onUsernameChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="email" className="col-form-label col-5">{text.emailAddress}</label>
                        <div className="col-7">
                            <input id="email" className="form-control" type="text" value={userEmail} onChange={onEmailChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="firstname" className="col-form-label col-5">{text.firstName}</label>
                        <div className="col-7">
                            <input id="firstname" className="form-control" type="text" value={userFirstname} onChange={onFirstnameChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="lastname" className="col-form-label col-5">{text.lastName}</label>
                        <div className="col-7">
                            <input id="lastname" className="form-control" type="text" value={userLastname} onChange={onLastnameChange} />
                        </div>
                    </div>
                    <div clasName="row">
                        <div className="col">
                            <div className="form-check">
                                <input id="administrator" className="form-check-input" type="checkbox" checked={userIsAdministrator} onChange={onUpdateUserIsAdministrator} />
                                <label htmlFor="administrator" className="form-check-label">{text.administrator}</label>
                            </div>
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={onSaveUserButtonClicked}>{text.saveUserModifications}</button>
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
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminUsersModify);
