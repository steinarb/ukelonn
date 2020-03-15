import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';

function AdminPaymenttypes(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { onLogout } = props;

    return (
        <div className="mdl-layout mdl-layout--fixed-header">
            <header className="mdl-layout__header">
                <div className="mdl-layout__header-row" style={reduceHeaderRowPadding}>
                    <Link to="/ukelonn/admin" className="mdl-navigation__link">
                        <i className="material-icons" >chevron_left</i>
                        &nbsp;
                        Registrer betaling
                    </Link>
                    <span className="mdl-layout-title">Administrere utbetalingstyper</span>
                </div>
            </header>
            <main className="mdl-layout__content">
                <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to="/ukelonn/admin/paymenttypes/modify">
                    Endre utbetalingstyper
                    <i className="material-icons">chevron_right</i>
                </Link>
                <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to="/ukelonn/admin/paymenttypes/create">
                    Lag ny utbetalingstype
                    <i className="material-icons">chevron_right</i>
                </Link>
            </main>
            <br/>
            <br/>
            <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onLogout()}>Logout</button>
            <br/>
            <a href="../../..">Tilbake til topp</a>
        </div>
    );
}

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

export default connect(mapStateToProps, mapDispatchToProps)(AdminPaymenttypes);
