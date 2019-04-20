import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';

class AdminUsers extends Component {
    render() {
        let { haveReceivedResponseFromLogin, loginResponse, onLogout } = this.props;

        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        return (
            <div>
                <h1>Administrere brukere</h1>
                <br/>
                <Link to="/ukelonn/admin">Registrer betaling</Link><br/>
                <br/>
                <Link to="/ukelonn/admin/users/modify">Endre brukere</Link>
                <br/>
                <Link to="/ukelonn/admin/users/password">Bytt passord på bruker</Link>
                <br/>
                <Link to="/ukelonn/admin/users/create">Legg til ny bruker</Link>
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

AdminUsers = connect(mapStateToProps, mapDispatchToProps)(AdminUsers);

export default AdminUsers;
