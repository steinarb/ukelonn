import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    MODIFY_PAYMENT_AMOUNT,
    REGISTERPAYMENT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import BonusBanner from './BonusBanner';
import Accounts from './Accounts';
import Paymenttypes from './Paymenttypes';
import EarningsMessage from './EarningsMessage';

function Admin(props) {
    const {
        text,
        accountId,
        username,
        balance,
        transactionTypeId,
        transactionAmount,
        onAmountFieldChange,
        onRegisterPayment,
        onLogout
    } = props;

    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    const parentTitle = 'Tilbake til ukelonn admin';
    const noUser = !username;
    const performedjobs = noUser ? '#' : '/ukelonn/performedjobs?' + stringify({ parentTitle, accountId, username });
    const performedpayments = noUser ? '#' : '/ukelonn/performedpayments?' + stringify({ parentTitle, accountId, username });
    const statistics = noUser ? '#' : '/ukelonn/statistics?' + stringify({ username });

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
                    <div className="form-group row">
                        <label htmlFor="account-selector" className="col-form-label col-5">{text.chooseWhoToPayTo}:</label>
                        <div className="col-7">
                            <Accounts id="account-selector" className="form-control" />
                        </div>
                    </div>
                    <div className="row">
                        <EarningsMessage />
                    </div>
                    <div className="form-group row">
                        <label htmlFor="account-balance" className="col-form-label col-5">{text.owedAmount}:</label>
                        <div className="col-7">
                            <input id="account-balance" className="form-control" type="text" value={balance} readOnly={true} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="paymenttype-selector" className="col-form-label col-5">{text.paymentType}:</label>
                        <div className="col-7">
                            <Paymenttypes id="paymenttype-selector" className="form-control" />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">{text.amount}:</label>
                        <div className="col-7">
                            <input id="amount" className="form-control" type="text" value={transactionAmount} onChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5" />
                        <div className="col-7">
                            <button className="btn btn-primary" disabled={noUser} onClick={() => onRegisterPayment({ account: { accountId, username }, transactionTypeId, transactionAmount })}>{text.registerPayment}</button>
                        </div>
                    </div>
                </div>
            </form>
            <div className="container">
                <Link className="btn btn-block btn-primary right-align-cell" to={performedjobs}>
                    {text.performedJobs}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to={performedpayments}>
                    {text.performedPayments}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to={statistics}>
                    {text.statistics}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/jobtypes">
                    {text.administrateJobsAndJobTypes}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/paymenttypes">
                    {text.administratePaymenttypes}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/users">
                    {text.administrateUsers}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/bonuses">
                    {text.administrateBonuses}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
            </div>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>{text.logout}</button>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        accountId: state.accountId,
        username: state.accountUsername,
        balance: state.accountBalance,
        transactionTypeId: state.transactionTypeId,
        transactionAmount: state.transactionAmount,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
        onAmountFieldChange: e => dispatch(MODIFY_PAYMENT_AMOUNT(e.target.value)),
        onRegisterPayment: (payment) => dispatch(REGISTERPAYMENT_REQUEST(payment)),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Admin);
