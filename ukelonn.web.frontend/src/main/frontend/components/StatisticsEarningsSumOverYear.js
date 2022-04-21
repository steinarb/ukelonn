import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import { findUsernameFromAccountOrQueryParameter } from '../common/account';
import Locale from './Locale';
import Logout from './Logout';

function StatisticsEarningsSumOverYear(props) {
    const {
        text,
        earningsSumOverYear,
    } = props;

    const username = findUsernameFromAccountOrQueryParameter(props);
    const statistics = '/ukelonn/statistics?' + stringify({ username });

    return (
        <div>
            <nav>
                <Link to={statistics}>
                    &lt;-
                    &nbsp;
                    {text.backToStatistics}
                </Link>
                <h1>{text.sumAmountEarnedPerYear}</h1>
                <Locale />
            </nav>
            <div>
                <table className="table table-bordered">
                    <thead>
                        <tr>
                            <th>{text.year}</th>
                            <th>{text.totalEarnings}</th>
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
            <Logout/>
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
        earningsSumOverYear: state.earningsSumOverYear,
    };
}

export default connect(mapStateToProps)(StatisticsEarningsSumOverYear);
