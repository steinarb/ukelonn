import React from 'react';
import { useSelector } from 'react-redux';
import { Link, useSearchParams } from 'react-router-dom';
import { stringify } from 'qs';
import { findUsernameFromAccountOrQueryParameter } from '../common/account';
import Locale from './Locale';
import Logout from './Logout';

export default function StatisticsEarningsSumOverYear(props) {
    const text = useSelector(state => state.displayTexts);
    const earningsSumOverYear = useSelector(state => state.earningsSumOverYear);

    const [ queryParams ] = useSearchParams();
    const username = findUsernameFromAccountOrQueryParameter(props, queryParams);
    const statistics = '/ukelonn/statistics?' + stringify({ username });

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to={statistics}>
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.backToStatistics}
                </Link>
                <h1>{text.sumAmountEarnedPerYear}</h1>
                <Locale />
            </nav>
            <div className="table-responsive table-sm table-striped">
                <table className="table">
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
