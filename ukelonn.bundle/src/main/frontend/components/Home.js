import React, { Component } from 'react';
import { Redirect } from 'react-router';
import { connect } from 'react-redux';

let Home = ({loginResponse}) => {
    if (loginResponse.roles.length > 0) {
        if (loginResponse.roles[0] === 'administrator') {
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

const mapStateToProps = state => {
    return {
        loginResponse: state.loginResponse
    };
};

Home = connect(mapStateToProps)(Home);

export default Home;
