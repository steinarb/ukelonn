import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import { findUsernameFromAccountOrQueryParameter } from '../common/account';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';

function Statistics(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, onLogout } = props;
    const username = findUsernameFromAccountOrQueryParameter(props);
    const sumoveryear = '/ukelonn/statistics/earnings/sumoveryear?' + stringify({ username });
    const sumovermonth = '/ukelonn/statistics/earnings/sumovermonth?' + stringify({ username });

    return (
        <div>
            <nav>
                <Link to="/ukelonn/">
                    &lt;-
                    &nbsp;
                    {text.back}
                </Link>
                <h1>{text.workStatistics}</h1>
                <Locale />
            </nav>
            <div className="container">
                <Link className="btn btn-block btn-primary right-align-cell" to={sumoveryear}>
                    {text.sumEarnedPerYear}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to={sumovermonth}>
                    {text.sumEarnedPerMonth}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
            </div>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>{text.logout}</button>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Statistics);
