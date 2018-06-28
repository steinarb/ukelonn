import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';

let User = ({loginResponse, onLogout}) => {
    if (loginResponse.roles.length === 0) {
        return <Redirect to="/ukelonn/login" />;
    }

    return (
        <div>
            <h1>Ukelønn user GUI</h1>
            <button onClick={() => onLogout()}>Logout</button>
        </div>
    );
};

const mapStateToProps = state => {
    return {
        loginResponse: state.loginResponse
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' })
    };
};

User = connect(mapStateToProps, mapDispatchToProps)(User);

export default User;
