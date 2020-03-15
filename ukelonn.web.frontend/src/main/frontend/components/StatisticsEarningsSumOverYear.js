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

function StatisticsEarningsSumOverYear(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { earningsSumOverYear, onLogout } = props;

    const username = findUsernameFromAccountOrQueryParameter(props);
    const statistics = '/ukelonn/statistics?' + stringify({ username });

    return (
        <div>
            <h1>Sum av lønn pr år</h1>
            <br/>
            <Link to={statistics}>Tilbake til statistikk</Link><br/>
            <table className="table table-bordered">
                <thead>
                    <tr>
                        <td>År</td>
                        <td>Totalt tjent</td>
                    </tr>
                </thead>
                <tbody>
                    {earningsSumOverYear.map((sumOverYear) =>
                        <tr key={sumOverYear.year}>
                            <td>{sumOverYear.year}</td>
                            <td>{sumOverYear.sum}</td>
                        </tr>
                    )}
                </tbody>
            </table>
            <br/>
            <button onClick={() => onLogout()}>Logout</button>
            <br/>
            <a href="../../..">Tilbake til topp</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        account: state.account,
        earningsSumOverYear: state.earningsSumOverYear,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(StatisticsEarningsSumOverYear);
