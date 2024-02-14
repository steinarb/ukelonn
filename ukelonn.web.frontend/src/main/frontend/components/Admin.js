import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import {
    MODIFY_PAYMENT_AMOUNT,
    REGISTER_PAYMENT_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import BonusBanner from './BonusBanner';
import Accounts from './Accounts';
import Paymenttypes from './Paymenttypes';
import EarningsMessage from './EarningsMessage';
import Logout from './Logout';

export default function Admin() {
    const text = useSelector(state => state.displayTexts);
    const accountId = useSelector(state => state.accountId);
    const username = useSelector(state => state.accountUsername);
    const balance = useSelector(state => state.accountBalance);
    const transactionAmount = useSelector(state => state.transactionAmount);
    const dispatch = useDispatch();
    const parentTitle = 'Tilbake til ukelonn admin';
    const noUser = !username;
    const performedjobs = noUser ? '#' : '/performedjobs?' + stringify({ parentTitle, accountId, username });
    const performedpayments = noUser ? '#' : '/performedpayments?' + stringify({ parentTitle, accountId, username });
    const statistics = noUser ? '#' : '/statistics?' + stringify({ username });

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <a className="btn btn-primary" href="../.."><span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>&nbsp;{text.returnToTop}</a>
                <h1>{text.registerPayment}</h1>
                <Locale />
            </nav>
            <div>
                <BonusBanner/>
            </div>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row mb-2">
                        <label htmlFor="account-selector" className="col-form-label col-5">{text.chooseWhoToPayTo}:</label>
                        <div className="col-7">
                            <Accounts id="account-selector" className="form-control" />
                        </div>
                    </div>
                    <div className="row">
                        <EarningsMessage />
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="account-balance" className="col-form-label col-5">{text.owedAmount}:</label>
                        <div className="col-7">
                            <input id="account-balance" className="form-control" type="text" value={balance} readOnly={true} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="paymenttype-selector" className="col-form-label col-5">{text.paymentType}:</label>
                        <div className="col-7">
                            <Paymenttypes id="paymenttype-selector" className="form-control" />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="amount" className="col-form-label col-5">{text.amount}:</label>
                        <div className="col-7">
                            <input
                                id="amount"
                                className="form-control"
                                type="text"
                                value={transactionAmount}
                                onChange={e => dispatch(MODIFY_PAYMENT_AMOUNT(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <div className="col-5" />
                        <div className="col-7">
                            <button
                                className="btn btn-primary"
                                disabled={noUser}
                                onClick={() => dispatch(REGISTER_PAYMENT_BUTTON_CLICKED())}>
                                {text.registerPayment}
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
                <Link className="btn btn-block btn-primary right-align-cell mb-2" to="/admin/jobtypes">
                    {text.administrateJobsAndJobTypes}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell mb-2" to="/admin/paymenttypes">
                    {text.administratePaymenttypes}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell mb-2" to="/admin/users">
                    {text.administrateUsers}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell mb-2" to="/admin/bonuses">
                    {text.administrateBonuses}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
            </div>
            <br/>
            <Logout />
        </div>
    );
}
