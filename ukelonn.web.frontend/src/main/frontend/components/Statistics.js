import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import { findUsernameFromAccountOrQueryParameter } from '../common/account';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';

class Statistics extends Component {
    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let { onLogout } = this.props;
        const username = findUsernameFromAccountOrQueryParameter(this.props);
        const sumoveryear = '/ukelonn/statistics/earnings/sumoveryear?' + stringify({ username });
        const sumovermonth = '/ukelonn/statistics/earnings/sumovermonth?' + stringify({ username });

        return (
            <div>
                <h1>Jobbstatistikk</h1>
                <br/>
                <Link to="/ukelonn/">Tilbake</Link><br/>
                <Link to={sumoveryear}>Sum av beløp tjent pr. år</Link><br/>
                <Link to={sumovermonth}>Sum av beløp tjent pr. år og måned</Link><br/>
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

Statistics = connect(mapStateToProps, mapDispatchToProps)(Statistics);

export default Statistics;
