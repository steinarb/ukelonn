import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import {
    MODIFY_JOB_DATE,
    REGISTER_JOB_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import BonusBanner from './BonusBanner';
import Jobtypes from './Jobtypes';
import Notification from './Notification';
import EarningsMessage from './EarningsMessage';
import Logout from './Logout';

export default function User() {
    const text = useSelector(state => state.displayTexts);
    const accountId = useSelector(state => state.accountId);
    const firstname = useSelector(state => state.accountFirstname);
    const username = useSelector(state => state.accountUsername);
    const accountBalance = useSelector(state => state.accountBalance);
    const transactionAmount = useSelector(state => state.transactionAmount);
    const transactionDate = useSelector(state => state.transactionDate.split('T')[0]);
    const notificationMessage = useSelector(state => state.notificationMessage);
    const dispatch = useDispatch();
    const title = text.weeklyAllowanceFor + ' ' + firstname;
    const performedjobs = '/ukelonn/performedjobs?' + stringify({ accountId, username, parentTitle: title });
    const performedpayments = '/ukelonn/performedpayments?' + stringify({ accountId, username, parentTitle: title });
    const statistics = '/ukelonn/statistics?' + stringify({ username });

    return (
        <div>
            <Notification notificationMessage={notificationMessage}/>
            <nav className="navbar navbar-light bg-light">
                <a className="btn btn-primary" href="../.."><span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>&nbsp;{text.returnToTop}</a>
                <h1 id="logo">{title}</h1>
                <Locale />
            </nav>
            <div className="container-fluid">
                <BonusBanner/>
                <div className="container">
                    <div className="row border rounded mb-3">
                        <div className="col">
                            <label>{text.owedAmount}:</label>
                        </div>
                        <div className="col">
                            {accountBalance}
                        </div>
                    </div>
                    <div className="row">
                        <EarningsMessage />
                    </div>
                </div>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="container">
                        <div className="form-group row mb-2">
                            <label htmlFor="jobtype" className="col-form-label col-5">{text.chooseJob}</label>
                            <div className="col-7">
                                <Jobtypes id="jobtype" className="form-control" />
                            </div>
                        </div>
                        <div className="form-group row mb-2">
                            <label htmlFor="amount" className="col-form-label col-5">{text.amount}</label>
                            <div className="col-7">
                                <input id="amount" className="form-control" type="text" value={transactionAmount} readOnly={true} />
                            </div>
                        </div>
                        <div className="form-group row mb-2">
                            <label htmlFor="date" className="col-form-label col-5">{text.date}</label>
                            <div className="col-7">
                                <input
                                    id="date"
                                    className="form-control"
                                    type="date"
                                    value={transactionDate}
                                    onChange={e => dispatch(MODIFY_JOB_DATE(e.target.value))}
                                />
                            </div>
                        </div>
                        <div className="form-group row mb-2">
                            <div className="col-5"/>
                            <div className="col-7">
                                <button
                                    className="btn btn-primary"
                                    onClick={() => dispatch(REGISTER_JOB_BUTTON_CLICKED())}>
                                    {text.registerJob}
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
                <div className="container">
                    <Link className="btn btn-block btn-primary right-align-cell mb-2" to={performedjobs}>
                        {text.performedJobs}
                        &nbsp;
                        <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                    </Link>
                    <Link className="btn btn-block btn-primary right-align-cell mb-2" to={performedpayments}>
                        {text.performedPayments}
                        &nbsp;
                        <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                    </Link>
                    <Link className="btn btn-block btn-primary right-align-cell mb-2" to={statistics}>
                        {text.statistics}
                        &nbsp;
                        <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                    </Link>
                </div>
                <br/>
                <br/>
                <Logout />
            </div>
        </div>
    );
}
