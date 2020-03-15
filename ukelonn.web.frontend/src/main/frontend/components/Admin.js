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
import Accounts from './Accounts';
import Paymenttypes from './Paymenttypes';
import Amount from './Amount';
import EarningsMessage from './EarningsMessage';

function Admin(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let {
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
            <a className="btn btn-block btn-primary left-align-cell" href="../.."><span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>&nbsp;Tilbake til topp</a>
            <header>
                <div className="pb-2 mt-4 mb-2 border-bottom bg-light">
                    <h1>Registrer betaling</h1>
                </div>
            </header>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="account-selector" className="col-form-label col-5">Velg hvem det skal betales til:</label>
                        <div className="col-7">
                            <Accounts id="account-selector" className="form-control" value={account.accountId} accounts={accounts} onAccountsFieldChange={onAccountsFieldChange}/>
                        </div>
                    </div>
                    <div className="row">
                        <EarningsMessage />
                    </div>
                    <div className="form-group row">
                        <label htmlFor="account-balance" className="col-form-label col-5">Til gode:</label>
                        <div className="col-7">
                            <input id="account-balance" className="form-control" type="text" value={account.balance} readOnly={true} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="paymenttype-selector" className="col-form-label col-5">Type av utbetaling:</label>
                        <div className="col-7">
                            <Paymenttypes id="paymenttype-selector" className="form-control" value={payment.transactionTypeId} paymenttypes={paymenttypes} account={account} onPaymenttypeFieldChange={onPaymenttypeFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">Beløp:</label>
                        <div className="col-7">
                            <Amount id="amount" className="form-control" payment={payment} onAmountFieldChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5" />
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => onRegisterPayment(payment, paymenttype)}>Registrer betaling</button>
                        </div>
                    </div>
                </div>
            </form>
            <div className="container">
                <Link className="btn btn-block btn-primary right-align-cell" to={performedjobs}>
                    Utforte jobber
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to={performedpayments}>
                    Siste utbetalinger til bruker
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to={statistics}>
                    Statistikk
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/jobtypes">
                    Administrere jobber og jobbtyper
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/paymenttypes">
                    Administrere utbetalingstyper
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/users">
                    Administrere brukere
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
            </div>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>Logout</button>
        </div>
    );
}

function mapStateToProps(state) {
    return {
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
