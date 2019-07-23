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
            <div>
                <h1>Administrer jobber og jobbtyper</h1>
                <br/>
                <Link to="/ukelonn/admin">Registrer betaling</Link><br/>
                <Link to="/ukelonn/admin/jobtypes/modify">Endre jobbtyper</Link><br/>
                <Link to="/ukelonn/admin/jobtypes/create">Lag ny jobbtype</Link><br/>
                <Link to="/ukelonn/admin/jobs/delete">Slett jobber</Link><br/>
                <Link to="/ukelonn/admin/jobs/edit">Endre jobber</Link><br/>
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
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
