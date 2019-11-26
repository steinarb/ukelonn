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
        <div>
          <h1>Administrer betalingstyper</h1>
          <br/>
          <Link to="/ukelonn/admin">Registrer betaling</Link><br/>
          <Link to="/ukelonn/admin/paymenttypes/modify">Endre utbetalingstyper</Link><br/>
          <Link to="/ukelonn/admin/paymenttypes/create">Lag ny utbetalingstype</Link><br/>
          <br/>
          <button onClick={() => onLogout()}>Logout</button>
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

AdminPaymenttypes = connect(mapStateToProps, mapDispatchToProps)(AdminPaymenttypes);

export default AdminPaymenttypes;
