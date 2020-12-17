import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';

function AdminUsers(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, onLogout } = props;

    return (
        <div>
            <Link to="/ukelonn/admin">
                &lt;-
                &nbsp;
                {text.registerPayment}
            </Link>
            <header>
                <div>
                    <h1>{text.administrateUsers}Administrere brukere</h1>
                </div>
            </header>
            <div>
                <Link to="/ukelonn/admin/users/modify">
                    {text.modifyUsers}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/ukelonn/admin/users/password">
                    {text.changeUsersPassword}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/ukelonn/admin/users/create">
                    {text.addUser}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
            </div>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../..">{text.returnToTop}</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminUsers);
