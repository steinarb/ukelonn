import React from 'react';
import { useSelector } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    useGetSumoveryearQuery,
} from '../api';
import { Link, useSearchParams } from 'react-router';
import { stringify } from 'qs';
import Locale from './Locale';
import Logout from './Logout';

export default function StatisticsEarningsSumOverYear(props) {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const [ queryParams ] = useSearchParams();
    const username = queryParams.get('username');
    const { data: earningsSumOverYear = [] } = useGetSumoveryearQuery(username);

    const statistics = '/statistics?' + stringify({ username });

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
