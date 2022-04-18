import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    USERS_REQUEST,
    MODIFY_PASSWORD1,
    MODIFY_PASSWORD2,
    CHANGE_PASSWORD_BUTTON_CLICKED,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import Users from './Users';

function AdminUsersChangePassword(props) {
    const {
        text,
        password1,
        password2,
        passwordsNotIdentical,
        onPassword1Change,
        onPassword2Change,
        onSaveUpdatedPassword,
        onLogout,
    } = props;

    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

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
                            <Users id="users" />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password1" className="col-form-label col-5">{text.password}:</label>
                        <div className="col-7">
                            <input id="password1" type='password' value={password1} onChange={onPassword1Change} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password2" className="col-form-label col-5">{text.repeatPassword}:</label>
                        <div className="col-7">
                            <input id="password2" type='password' value={password2} onChange={onPassword2Change} />
                            { passwordsNotIdentical && <span>{text.passwordsAreNotIdentical}</span> }
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button onClick={onSaveUpdatedPassword}>{text.changePassword}</button>
                        </div>
                    </div>
                </div>
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
        password1: state.password1,
        password2: state.password2,
        passwordsNotIdentical: state.passwordsNotIdentical,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onUserList: () => dispatch(USERS_REQUEST()),
        onPassword1Change: e => dispatch(MODIFY_PASSWORD1(e.target.value)),
        onPassword2Change: e => dispatch(MODIFY_PASSWORD2(e.target.value)),
        onSaveUpdatedPassword: () => dispatch(CHANGE_PASSWORD_BUTTON_CLICKED()),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminUsersChangePassword);
