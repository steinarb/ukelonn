import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import moment from 'moment';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    UPDATE_PERFORMEDJOB,
    REGISTERJOB_REQUEST,
} from '../actiontypes';
import BonusBanner from './BonusBanner';
import Jobtypes from './Jobtypes';
import Notification from './Notification';
import EarningsMessage from './EarningsMessage';

function User(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, account, jobtypes, performedjob, notificationMessage, onJobtypeFieldChange, onDateFieldChange, onRegisterJob, onLogout } = props;
    const title = text.weeklyAllowanceFor + ' ' + account.firstName;
    const username = account.username;
    const performedjobs = '/ukelonn/performedjobs?' + stringify({ accountId: account.accountId, username, parentTitle: title });
    const performedpayments = '/ukelonn/performedpayments?' + stringify({ accountId: account.accountId, username, parentTitle: title });
    const statistics = '/ukelonn/statistics?' + stringify({ username });

    return (
        <div>
            <Notification notificationMessage={notificationMessage}/>
            <h1>{title}</h1>
            <BonusBanner/>
            <div>{text.owedAmount} {account.balance}</div><br/>
            <EarningsMessage /><br/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <label htmlFor="jobtype">{text.chooseJob}</label>
                <Jobtypes id="jobtype" value={performedjob.transactionTypeId} jobtypes={jobtypes} onJobtypeFieldChange={onJobtypeFieldChange} />
                <br/>
                <label htmlFor="amount">{text.amount}</label>
                <input id="amount" type="text" value={performedjob.transactionAmount} readOnly={true} />
                <br/>
                <label htmlFor="date">{text.date}</label>
                <DatePicker selected={performedjob.transactionDate} dateFormat="YYYY-MM-DD" onChange={(selectedValue) => onDateFieldChange(selectedValue, performedjob)} readOnly={true} />
                <br/>

                <button onClick={() => onRegisterJob(performedjob)}>{text.registerJob}</button>
            </form>
            <br/>
            <Link to={performedjobs}>{text.performedJobs}</Link><br/>
            <Link to={performedpayments}>{text.performedPayments}</Link><br/>
            <Link to={statistics}>{text.statistics}</Link><br/>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../..">{text.returnToTop}</a>
        </div>
    );
}

const emptyJob = {
    account: { accountId: -1 },
    id: -1,
    transactionName: '',
    transactionAmount: 0.0
};

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        account: state.account,
        jobtypes: state.jobtypes,
        performedjob: state.performedjob,
        notificationMessage: state.notificationMessage,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
        onJobtypeFieldChange: (selectedValue, jobtypes) => {
            const selectedValueInt = parseInt(selectedValue, 10);
            let jobtype = jobtypes.find(jobtype => jobtype.id === selectedValueInt);
            dispatch(UPDATE_PERFORMEDJOB({
                transactionTypeId: selectedValue,
                transactionAmount: jobtype.transactionAmount,
                transactionDate: moment(),
            }));
        },
        onDateFieldChange: (selectedValue) => {
            dispatch(UPDATE_PERFORMEDJOB({
                transactionDate: selectedValue,
            }));
        },
        onRegisterJob: (performedjob) => dispatch(REGISTERJOB_REQUEST(performedjob)),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(User);
