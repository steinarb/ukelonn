import React from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import { findUsernameFromAccountOrQueryParameter } from '../common/account';
import Locale from './Locale';
import Logout from './Logout';

export default function Statistics(props) {
    const text = useSelector(state => state.displayTexts);
    const username = findUsernameFromAccountOrQueryParameter(props);
    const sumoveryear = '/ukelonn/statistics/earnings/sumoveryear?' + stringify({ username });
    const sumovermonth = '/ukelonn/statistics/earnings/sumovermonth?' + stringify({ username });

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.back}
                </Link>
                <h1>{text.workStatistics}</h1>
                <Locale />
            </nav>
            <div className="container">
                <Link className="btn btn-block btn-primary right-align-cell" to={sumoveryear}>
                    {text.sumEarnedPerYear}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to={sumovermonth}>
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
