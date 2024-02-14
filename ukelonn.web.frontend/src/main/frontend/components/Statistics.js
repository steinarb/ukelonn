import React from 'react';
import { useSelector } from 'react-redux';
import { Link, useSearchParams } from 'react-router-dom';
import { stringify } from 'qs';
import { findUsernameFromAccountOrQueryParameter } from '../common/account';
import Locale from './Locale';
import Logout from './Logout';

export default function Statistics(props) {
    const text = useSelector(state => state.displayTexts);
    const [ queryParams ] = useSearchParams();
    const username = findUsernameFromAccountOrQueryParameter(props, queryParams);
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
