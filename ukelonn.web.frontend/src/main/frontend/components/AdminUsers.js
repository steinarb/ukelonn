import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';

class AdminUsers extends Component {
    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let { onLogout } = this.props;

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
