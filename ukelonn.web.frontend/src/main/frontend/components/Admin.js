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
            <h1>{text.registerPayment}</h1>
            <BonusBanner/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <label htmlFor="account-selector">{text.chooseWhoToPayTo}:</label>
                <Accounts  id="account-selector" value={account.accountId} accounts={accounts} onAccountsFieldChange={onAccountsFieldChange}/>
                <br/>
                <EarningsMessage /><br/>
                <label htmlFor="account-balance">{text.owedAmount}:</label><input id="account-balance" type="text" value={account.balance} readOnly={true} /><br/>
                <label htmlFor="paymenttype-selector">{text.paymentType}:</label>
                <Paymenttypes id="paymenttype-selector" value={payment.transactionTypeId} paymenttypes={paymenttypes} account={account} onPaymenttypeFieldChange={onPaymenttypeFieldChange} />
                <br/>
                <label htmlFor="amount">{text.amount}:</label>
                <Amount id="amount" payment={payment} onAmountFieldChange={onAmountFieldChange} />
                <br/>
                <br/>
                <button disabled={noUser} onClick={() => onRegisterPayment(payment, paymenttype)}>{text.registerPayment}</button>
            </form>
            <br/>
            <Link to={performedjobs}>{text.performedJobs}</Link><br/>
            <Link to={performedpayments}>{text.performedPayments}</Link><br/>
            <Link to={statistics}>{text.statistics}</Link><br/>
            <Link to="/ukelonn/admin/jobtypes">{text.administrateJobsAndJobTypes}</Link><br/>
            <Link to="/ukelonn/admin/paymenttypes">{text.administratePaymenttypes}</Link><br/>
            <Link to="/ukelonn/admin/users">{text.administrateUsers}</Link><br/>
            <Link to="/ukelonn/admin/bonuses">{text.administrateBonuses}</Link><br/>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../..">{text.returnToTop}</a>
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
