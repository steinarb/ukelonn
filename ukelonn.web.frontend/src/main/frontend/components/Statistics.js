import React from 'react';
import { useSelector } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
} from '../api';
import { Link, useSearchParams } from 'react-router';
import { stringify } from 'qs';
import Locale from './Locale';
import Logout from './Logout';

export default function Statistics(props) {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const [ queryParams ] = useSearchParams();
    const username = queryParams.get('username');
    const sumoveryear = '/statistics/earnings/sumoveryear?' + stringify({ username });
    const sumovermonth = '/statistics/earnings/sumovermonth?' + stringify({ username });

    return (
        <div>
            <nav>
                <Link to="/">
                    &lt;-
                    &nbsp;
                    {text.back}
                </Link>
                <h1>{text.workStatistics}</h1>
                <Locale />
            </nav>
            <div>
                <Link to={sumoveryear}>
                    {text.sumEarnedPerYear}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to={sumovermonth}>
                    {text.sumEarnedPerMonth}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
            </div>
            <br/>
            <Logout />
            <br/>
            <a href="../../..">{text.returnToTop}</a>
        </div>
    );
}
