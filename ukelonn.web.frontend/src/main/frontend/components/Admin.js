import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    UPDATE_FIRSTTIMEAFTERLOGIN,
    UPDATE_ACCOUNT,
    UPDATE_PAYMENT,
    REGISTERPAYMENT_REQUEST,
} from '../actiontypes';
import { emptyAccount } from '../constants';
import Locale from './Locale';
import BonusBanner from './BonusBanner';
import Accounts from './Accounts';
import Paymenttypes from './Paymenttypes';
import Amount from './Amount';
import EarningsMessage from './EarningsMessage';

function Admin(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let {
        text,
        account = {},
        payment,
        paymenttype,
        amount,
        accounts,
        paymenttypes,
        onAccountsFieldChange,
        onPaymenttypeFieldChange,
        onAmountFieldChange,
        onRegisterPayment,
        onLogout } = props;

    const parentTitle = 'Tilbake til ukelonn admin';
    const accountId = account.accountId;
    const username = account.username;
    const noUser = !username;
    const performedjobs = noUser ? '#' : '/ukelonn/performedjobs?' + stringify({ parentTitle, accountId, username });
    const performedpayments = noUser ? '#' : '/ukelonn/performedpayments?' + stringify({ parentTitle, accountId, username });
    const statistics = noUser ? '#' : '/ukelonn/statistics?' + stringify({ username });

    return (
        <div>
            <a href="../..">&lt;-&nbsp;{text.returnToTop}</a>
            <header>
                <div>
                    <h1>{text.registerPayment}</h1>
                </div>
                <div>
                    <Locale />
                </div>
            </header>
            <div>
                <BonusBanner/>
            </div>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="account-selector">{text.chooseWhoToPayTo}:</label>
                        <div>
                            <Accounts  id="account-selector" value={account.accountId} accounts={accounts} onAccountsFieldChange={onAccountsFieldChange}/>
                        </div>
                    </div>
                    <EarningsMessage />
                    <br/>
                    <div>
                        <label htmlFor="account-balance">{text.owedAmount}:</label>
                        <div>
                            <input id="account-balance" type="text" value={account.balance} readOnly={true} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="paymenttype-selector">{text.paymentType}:</label>
                        <div>
                            <Paymenttypes id="paymenttype-selector" value={payment.transactionTypeId} paymenttypes={paymenttypes} account={account} onPaymenttypeFieldChange={onPaymenttypeFieldChange} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.amount}:</label>
                        <div>
                            <Amount id="amount" payment={payment} onAmountFieldChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div>
                        <div>
                            <button disabled={noUser} onClick={() => onRegisterPayment(payment, paymenttype)}>{text.registerPayment}</button>
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
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        account: state.account,
        payment: state.payment,
        paymenttype: state.paymenttype,
        accounts: state.accounts,
        paymenttypes: state.paymenttypes,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
        onAccountsFieldChange: (selectedValue, accounts) => {
            const selectedValueInt = parseInt(selectedValue, 10);
            let account = accounts.find(account => account.accountId === selectedValueInt);
            dispatch(UPDATE_ACCOUNT(account));
        },
        onPaymenttypeFieldChange: (selectedValue, paymenttypes, account) => {
            const selectedValueInt = parseInt(selectedValue, 10);
            let paymenttype = paymenttypes.find(pt => pt.id === selectedValueInt);
            let amount = (paymenttype.transactionAmount > 0) ? paymenttype.transactionAmount : account.balance;
            dispatch(UPDATE_PAYMENT({
                transactionTypeId: paymenttype.id,
                transactionAmount: amount,
                account: account,
            }));
        },
        onAmountFieldChange: (transactionAmount) => dispatch(UPDATE_PAYMENT({ transactionAmount })),
        onRegisterPayment: (payment) => dispatch(REGISTERPAYMENT_REQUEST({ ...payment })),
    };
}

Admin = connect(mapStateToProps, mapDispatchToProps)(Admin);

export default Admin;
