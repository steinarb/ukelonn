import React from 'react';
import { useSelector } from 'react-redux';
import { Link, useSearchParams } from 'react-router-dom';
import { stringify } from 'qs';
import { findUsernameFromAccountOrQueryParameter } from '../common/account';
import Locale from './Locale';
import Logout from './Logout';

export default function StatisticsEarningsSumOverMonth(props) {
    const text = useSelector(state => state.displayTexts);
    const earningsSumOverMonth = useSelector(state => state.earningsSumOverMonth);
    const [ queryParams ] = useSearchParams();
    const username = findUsernameFromAccountOrQueryParameter(props, queryParams);
    const statistics = '/statistics?' + stringify({ username });

    return (
        <div>
            <nav>
                <Link to={statistics}>
                    &lt;-
                    &nbsp;
                    {text.backToStatistics}
                </Link>
                <h1>{text.sumAmountEarnedPerMonthAndYear}</h1>
                <Locale />
            </nav>
            <div>
                <table className="table table-bordered">
                    <thead>
                        <tr>
                            <th>{text.year}</th>
                            <th>{text.month}</th>
                            <th>{text.totalAmountEarned}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {earningsSumOverMonth.map((sumOverMonth) =>
                                                  <tr key={''.concat(sumOverMonth.year, sumOverMonth.month)}>
                                                      <td>{sumOverMonth.year}</td>
                                                      <td>{sumOverMonth.month}</td>
                                                      <td>{sumOverMonth.sum}</td>
                                                  </tr>
                                                 )}
                    </tbody>
                </table>
            </div>
            <br/>
            <Logout />
            <br/>
            <a href="../../..">{text.returnToTop}</a>
        </div>
    );
}
