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
            <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/">
                <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.back}
            </Link>
            <header>
                <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                    <h1>{text.workStatistics}</h1>
                </div>
                <div>
                    <Locale />
                </div>
            </header>
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
