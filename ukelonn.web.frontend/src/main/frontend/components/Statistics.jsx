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
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.back}
                </Link>
                <h1>{text.workStatistics}</h1>
                <Locale />
            </nav>
            <div className="container">
                <Link className="btn btn-block btn-primary right-align-cell mb-2" to={sumoveryear}>
                    {text.sumEarnedPerYear}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell mb-2" to={sumovermonth}>
                    {text.sumEarnedPerMonth}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
            </div>
            <br/>
            <Logout />
        </div>
    );
}
