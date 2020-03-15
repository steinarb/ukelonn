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
        <div className="mdl-layout mdl-layout--fixed-header">
            <header className="mdl-layout__header">
                <div className="mdl-layout__header-row">
                    <span className="mdl-layout-title">Registrer betaling</span>
                    <div className="mdl-layout-spacer"></div>
                </div>
            </header>
            <main className="mdl-layout__content">
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="account-selector">Velg hvem det skal betales til:</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <Accounts id="account-selector" className="stretch-to-fill" value={account.accountId} accounts={accounts} onAccountsFieldChange={onAccountsFieldChange}/>
                        </div>
                    </div>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="account-balance">Til gode:</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <input id="account-balance" className='mdl-textfield__input stretch-to-fill' type="text" value={account.balance} readOnly={true} />
                        </div>
                    </div>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="paymenttype-selector">Type av utbetaling:</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <Paymenttypes id="paymenttype-selector" className="stretch-to-fill" value={payment.transactionTypeId} paymenttypes={paymenttypes} account={account} onPaymenttypeFieldChange={onPaymenttypeFieldChange} />
                        </div>
                    </div>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="amount">Beløp:</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <Amount id="amount" className="stretch-to-fill" payment={payment} onAmountFieldChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div className="mdl-grid mdl-grid--no-spacing hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--6-col-tablet mdl-cell--10-col-desktop">
                            &nbsp;
                        </div>
                        <div className="mdl-cell mdl-cell--2-col">
                            <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onRegisterPayment(payment, paymenttype)}>Registrer betaling</button>
                        </div>
                    </div>
                </form>
                <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to={performedjobs}>
                    Siste jobber for bruker
                    <i className="material-icons">chevron_right</i>
                </Link>
                <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to={performedpayments}>
                    Siste utbetalinger til bruker
                    <i className="material-icons">chevron_right</i>
                </Link>
                <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to="/ukelonn/admin/jobtypes">
                    Administrere jobber og jobbtyper
                    <i className="material-icons">chevron_right</i>
                </Link>
                <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to="/ukelonn/admin/paymenttypes">
                    Administrere utbetalingstyper
                    <i className="material-icons">chevron_right</i>
                </Link>
                <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to="/ukelonn/admin/users">
                    Administrere brukere
                    <i className="material-icons">chevron_right</i>
                </Link>
            </main>
            <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onLogout()}>Logout</button>
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
