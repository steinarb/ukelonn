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
                <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    Register betaling
                </Link>
                <header>
                    <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                        <h1>Administrere brukere</h1>
                    </div>
                </header>
                <div className="container">
                    <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/users/modify">
                        Endre brukere
                        &nbsp;
                        <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                    </Link>
                    <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/users/password">
                        Bytt passord på bruker
                        &nbsp;
                        <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                    </Link>
                    <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/users/create">
                        Legg til ny bruker
                        &nbsp;
                        <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                    </Link>
                </div>
                <br/>
                <button className="btn btn-default" onClick={() => onLogout()}>Logout</button>
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
