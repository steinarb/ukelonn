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
            <h1>{text.workStatistics}</h1>
            <br/>
            <Link to="/ukelonn/">{text.back}</Link><br/>
            <Link to={sumoveryear}>{text.sumEarnedPerYear}</Link><br/>
            <Link to={sumovermonth}>{text.sumEarnedPerMonth}</Link><br/>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../..">{text.returnToTop}</a>
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
