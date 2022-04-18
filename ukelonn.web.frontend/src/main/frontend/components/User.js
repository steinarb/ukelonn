import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    MODIFY_JOB_DATE,
    REGISTER_JOB_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import BonusBanner from './BonusBanner';
import Jobtypes from './Jobtypes';
import Notification from './Notification';
import EarningsMessage from './EarningsMessage';

function User(props) {
    const {
        text,
        accountId,
        firstname,
        username,
        accountBalance,
        transactionAmount,
        transactionDate,
        notificationMessage,
        onDateFieldChange,
        onRegisterJob,
        onLogout,
    } = props;
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

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
                        <div className="form-group row">
                            <label htmlFor="jobtype" className="col-form-label col-5">{text.chooseJob}</label>
                            <div className="col-7">
                                <Jobtypes id="jobtype" className="form-control" />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="amount" className="col-form-label col-5">{text.amount}</label>
                            <div className="col-7">
                                <input id="amount" className="form-control" type="text" value={transactionAmount} readOnly={true} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="date" className="col-form-label col-5">{text.date}</label>
                            <div className="col-7">
                                <DatePicker selected={new Date(transactionDate)} dateFormat="yyyy-MM-dd" onChange={(selectedValue) => onDateFieldChange(selectedValue)} onFocus={e => e.target.blur()} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <div className="col-5"/>
                            <div className="col-7">
                                <button className="btn btn-primary" onClick={onRegisterJob}>{text.registerJob}</button>
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
                </div>
                <br/>
                <br/>
                <button className="btn btn-default" onClick={() => onLogout()}>{text.logout}</button>
            </div>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        accountId: state.accountId,
        firstname: state.accountFirstname,
        username: state.accountUsername,
        accountBalance: state.accountBalance,
        transactionAmount: state.transactionAmount,
        transactionDate: state.transactionDate,
        notificationMessage: state.notificationMessage,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
        onDateFieldChange: (selectedValue) => dispatch(MODIFY_JOB_DATE(selectedValue)),
        onRegisterJob: () => dispatch(REGISTER_JOB_BUTTON_CLICKED()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(User);
