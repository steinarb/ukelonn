import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';

class AdminJobtypes extends Component {
    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let { onLogout } = this.props;

        return (
            <div className="mdl-layout mdl-layout--fixed-header">
                <header className="mdl-layout__header">
                    <div className="mdl-layout__header-row" style={reduceHeaderRowPadding}>
                        <Link to="/ukelonn/admin" className="mdl-navigation__link">
                            <i className="material-icons" >chevron_left</i>
                            &nbsp;
                            Registrer betaling
                        </Link>
                        <span className="mdl-layout-title">Administrer jobbtyper</span>
                    </div>
                </header>
                <main className="mdl-layout__content">
                    <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to="/ukelonn/admin/jobtypes/modify">
                        Endre jobbtyper
                        <i className="material-icons">chevron_right</i>
                    </Link>
                    <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to="/ukelonn/admin/jobtypes/create">
                        Lag ny jobbtype
                        <i className="material-icons">chevron_right</i>
                    </Link>
                    <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to="/ukelonn/admin/jobs/delete">
                        Slett jobber
                        <i className="material-icons">chevron_right</i>
                    </Link>
                    <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to="/ukelonn/admin/jobs/edit">
                        Endre jobber
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
    };
};

const mapStateToProps = state => {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
};

AdminJobtypes = connect(mapStateToProps, mapDispatchToProps)(AdminJobtypes);

export default AdminJobtypes;
