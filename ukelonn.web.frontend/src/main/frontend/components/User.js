import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import moment from 'moment';
import {
    LOGOUT_REQUEST,
    ACCOUNT_REQUEST,
    START_NOTIFICATION_LISTENING,
    JOBTYPELIST_REQUEST,
    UPDATE,
    REGISTERJOB_REQUEST,
} from '../actiontypes';
import Jobtypes from './Jobtypes';
import Notification from './Notification';

class User extends Component {
    componentDidMount() {
        this.props.onAccount(this.props.loginResponse.username);
        this.props.onNotifyStart(this.props.loginResponse.username);
        this.props.onJobtypeList();
    }

    render() {
        let { loginResponse, account, jobtypes, jobtypesMap, performedjob, notificationMessage, earningsSumOverYear, earningsSumOverMonth, onJobtypeFieldChange, onDateFieldChange, onRegisterJob, onLogout } = this.props;
        if (loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        const title = 'Ukelønn for ' + account.firstName;
        const username = account.username;
        const performedjobs = '/ukelonn/performedjobs?' + stringify({ accountId: account.accountId, username, parentTitle: title });
        const performedpayments = '/ukelonn/performedpayments?' + stringify({ accountId: account.accountId, username, parentTitle: title });
        const statistics = '/ukelonn/statistics?' + stringify({ username });
        const earningsStatisticsMessage = createEarningsStatisticsMessage(earningsSumOverYear, earningsSumOverMonth);

        return (
            <div>
                <Notification notificationMessage={notificationMessage}/>
                <h1>{title}</h1>
                <div>Til gode: { account.balance }</div><br/>
                <div>{earningsStatisticsMessage}</div><br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="jobtype">Velg jobb</label>
                    <Jobtypes id="jobtype" jobtypes={jobtypes} jobtypesMap={jobtypesMap} value={performedjob.transactionName} account={account} performedjob={performedjob} onJobtypeFieldChange={onJobtypeFieldChange} />
                    <br/>
                    <label htmlFor="amount">Beløp</label>
                    <input id="amount" type="text" value={performedjob.transactionAmount} readOnly="true" />
                    <br/>
                    <label htmlFor="date">Dato</label>
                    <DatePicker selected={performedjob.transactionDate} dateFormat="YYYY-MM-DD" onChange={(selectedValue) => onDateFieldChange(selectedValue, performedjob)} readOnly={true} />
                    <br/>

                    <button onClick={() => onRegisterJob(performedjob)}>Registrer jobb</button>
                </form>
                <br/>
                <Link to={performedjobs}>Utforte jobber</Link><br/>
                <Link to={performedpayments}>Siste utbetalinger til bruker</Link><br/>
                <Link to={statistics}>Statistikker</Link><br/>
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
                <br/>
                <a href="../..">Tilbake til topp</a>
            </div>
        );
    }
};

const emptyJob = {
    account: { accountId: -1 },
    id: -1,
    transactionName: '',
    transactionAmount: 0.0
};

const mapStateToProps = state => {
    return {
        loginResponse: state.loginResponse,
        account: state.account,
        jobtypes: state.jobtypes,
        jobtypesMap: state.jobtypesMap,
        performedjob: state.performedjob,
        notificationMessage: state.notificationMessage,
        earningsSumOverYear: state.earningsSumOverYear,
        earningsSumOverMonth: state.earningsSumOverMonth,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
        onAccount: (username) => dispatch(ACCOUNT_REQUEST(username)),
        onNotifyStart: (username) => dispatch(START_NOTIFICATION_LISTENING(username)),
        onJobtypeList: () => dispatch(JOBTYPELIST_REQUEST()),
        onJobtypeFieldChange: (selectedValue, jobtypesMap, account, performedjob) => {
            let jobtype = jobtypesMap.get(selectedValue);
            let changedField = {
                performedjob: {
                    ...performedjob,
                    transactionTypeId: jobtype.id,
                    transactionName: jobtype.transactionName,
                    transactionAmount: jobtype.transactionAmount,
                    account: account,
                    transactionDate: moment(),
                }
            };
            dispatch(UPDATE(changedField));
        },
        onDateFieldChange: (selectedValue, performedjob) => {
            let changedField = {
                performedjob: {
                    ...performedjob,
                    transactionDate: selectedValue,
                }
            };
            dispatch(UPDATE(changedField));
        },
        onRegisterJob: (performedjob) => dispatch(REGISTERJOB_REQUEST(performedjob)),
    };
};

User = connect(mapStateToProps, mapDispatchToProps)(User);

export default User;

function createEarningsStatisticsMessage(earningsSumOverYear, earningsSumOverMonth) {
    if (!(earningsSumOverYear.length || earningsSumOverMonth.length)) {
        return '';
    }

    const yearMessage = messageForEarningsCurrentAndPreviousYear(earningsSumOverYear);
    const monthMessage = messageForEarningsCurrentMonthAndPreviousMonth(earningsSumOverMonth);
    return (<div>{yearMessage}{monthMessage}</div>);
}

function messageForEarningsCurrentAndPreviousYear(earningsSumOverYear) {
    let message = '';
    if (earningsSumOverYear.length) {
        const totalEarningsThisYear = earningsSumOverYear[earningsSumOverYear.length - 1].sum;
        message = 'Totalt beløp tjent i år: ' + totalEarningsThisYear;
        if (earningsSumOverYear.length > 1) {
            const previousYear = earningsSumOverYear[earningsSumOverYear.length - 2];
            message = message.concat(' (mot ', previousYear.sum, ' tjent i hele ', previousYear.year, ')');
        }
    }

    return (<div>{message}</div>);
}

function messageForEarningsCurrentMonthAndPreviousMonth(earningsSumOverMonth) {
    let message = '';
    if (earningsSumOverMonth.length) {
        const totalEarningsThisMonth = earningsSumOverMonth[earningsSumOverMonth.length - 1].sum;
        message = message.concat('Totalt beløp tjent denne måneden: ', totalEarningsThisMonth);
        if (earningsSumOverMonth.length > 1) {
            // The previous month may not actually be the previous month if the kids have been lazy
            // but we do care about that level of detail here (or at least: I don't...)
            const previousMonth = earningsSumOverMonth[earningsSumOverMonth.length - 2];
            message = message.concat(' (mot ', previousMonth.sum, ' tjent i hele forrige måned)');
        }
    }

    return (<div>{message}</div>);
}
