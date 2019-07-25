import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import { findUsernameFromAccountOrQueryParameter } from '../common/account';
import { userIsNotLoggedIn } from '../common/login';
import {
    ACCOUNT_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';

class StatisticsEarningsSumOverYear extends Component {
    componentDidMount() {
        const username = findUsernameFromAccountOrQueryParameter(this.props);
        this.props.onAccount(username);
    }

    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let { earningsSumOverYear, onLogout } = this.props;

        const username = findUsernameFromAccountOrQueryParameter(this.props);
        const statistics = '/ukelonn/statistics?' + stringify({ username });

        return (
            <div>
                <Link className="btn btn-block btn-primary mb-0 left-align-cell" to={statistics}>
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    Tilbake til statistikk
                </Link>
                <header>
                    <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                        <h1>Sum av lønn pr år</h1>
                    </div>
                </header>
                <div className="table-responsive table-sm table-striped">
                    <table className="table">
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
        account: state.account,
        earningsSumOverYear: state.earningsSumOverYear,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onAccount: (username) => dispatch(ACCOUNT_REQUEST(username)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
};

StatisticsEarningsSumOverYear = connect(mapStateToProps, mapDispatchToProps)(StatisticsEarningsSumOverYear);

export default StatisticsEarningsSumOverYear;
