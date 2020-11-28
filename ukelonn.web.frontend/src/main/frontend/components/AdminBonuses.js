import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';

function AdminBonuses(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { onLogout } = props;

    return (
        <div>
            <Link to="/ukelonn/">
                &lt;-
                &nbsp;
                Register betaling
            </Link>
            <header>
                <div>
                    <h1>Administrer bonuser</h1>
                </div>
            </header>
            <div>
                <Link to="/ukelonn/admin/bonuses/modify">
                    Endre bonuser
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/ukelonn/admin/bonuses/create">
                    Lag ny bonus
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/ukelonn/admin/bonuses/delete">
                    Slett bonus
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
            </div>
            <br/>
            <br/>
            <button onClick={() => onLogout()}>Logout</button>
            <br/>
            <a href="../../..">Tilbake til topp</a>
        </div>
    );
};

function mapStateToProps(state) {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminBonuses);
