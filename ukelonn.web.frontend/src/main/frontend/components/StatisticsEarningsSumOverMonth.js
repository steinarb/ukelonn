import React from 'react';
import { useSelector } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    useGetSumovermonthQuery,
} from '../api';
import { Link, useSearchParams } from 'react-router';
import { stringify } from 'qs';
import Locale from './Locale';
import Logout from './Logout';

export default function StatisticsEarningsSumOverMonth(props) {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const [ queryParams ] = useSearchParams();
    const username = queryParams.get('username');
    const { data: earningsSumOverMonth = [] } = useGetSumovermonthQuery(username);
    const statistics = '/statistics?' + stringify({ username });

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to={statistics}>
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.backToStatistics}
                </Link>
                <h1>{text.sumAmountEarnedPerMonthAndYear}</h1>
                <Locale />
            </nav>
            <div className="table-responsive table-sm table-striped">
                <table className="table">
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
