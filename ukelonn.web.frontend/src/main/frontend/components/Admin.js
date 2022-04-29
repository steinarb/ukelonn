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
    const performedjobs = noUser ? '#' : '/ukelonn/performedjobs?' + stringify({ parentTitle, accountId, username });
    const performedpayments = noUser ? '#' : '/ukelonn/performedpayments?' + stringify({ parentTitle, accountId, username });
    const statistics = noUser ? '#' : '/ukelonn/statistics?' + stringify({ username });

    return (
        <div>
            <nav>
                <a href="../..">&lt;-&nbsp;{text.returnToTop}</a>
                <h1>{text.registerPayment}</h1>
                <Locale />
            </nav>
            <div>
                <BonusBanner/>
            </div>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="account-selector">{text.chooseWhoToPayTo}:</label>
                        <div>
                            <Accounts id="account-selector" />
                        </div>
                    </div>
                    <EarningsMessage />
                    <br/>
                    <div>
                        <label htmlFor="account-balance">{text.owedAmount}:</label>
                        <div>
                            <input id="account-balance" type="text" value={balance} readOnly={true} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="paymenttype-selector">{text.paymentType}:</label>
                        <div>
                            <Paymenttypes id="paymenttype-selector" />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.amount}:</label>
                        <div>
                            <input
                                id="amount"
                                type="text"
                                value={transactionAmount}
                                onChange={e => dispatch(MODIFY_PAYMENT_AMOUNT(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <div>
                            <button
                                disabled={noUser}
                                onClick={() => dispatch(REGISTER_PAYMENT_BUTTON_CLICKED())}>
                                {text.registerPayment}
                            </button>
                        </div>
                    </div>
                </div>
            </form>
            <div>
                <Link to={performedjobs}>
                    {text.performedJobs}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to={performedpayments}>
                    {text.performedPayments}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to={statistics}>
                    {text.statistics}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/ukelonn/admin/jobtypes">
                    {text.administrateJobsAndJobTypes}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/ukelonn/admin/paymenttypes">
                    {text.administratePaymenttypes}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/ukelonn/admin/users">
                    {text.administrateUsers}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/ukelonn/admin/bonuses">
                    {text.administrateBonuses}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
            </div>
            <br/>
            <Logout />
            <br/>
        </div>
    );
}
