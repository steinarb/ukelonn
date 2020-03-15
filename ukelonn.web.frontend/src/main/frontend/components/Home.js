import React, { Component } from 'react';
import { Redirect } from 'react-router';
import { connect } from 'react-redux';

function Home(props) {
    const { loginResponse } = props;
    if (loginResponse.roles.length > 0) {
        if (loginResponse.roles[0] === 'ukelonnadmin') {
            return <Redirect to="/ukelonn/admin" />;
        }

        return <Redirect to="/ukelonn/user" />;
    }

    return (
        <div>
            <h1>Ukelønn hjem</h1>
        </div>
    );
};

function mapStateToProps(state) {
    return {
        loginResponse: state.loginResponse
    };
}

export default connect(mapStateToProps)(Home);
