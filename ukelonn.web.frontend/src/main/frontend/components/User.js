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
import Locale from './Locale';
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
            <a href="../..">&lt;-&nbsp;{text.returnToTop}</a>
            <Notification notificationMessage={notificationMessage}/>
            <header>
                <div>
                    <h1>{title}</h1>
                </div>
                <div>
                    <Locale />
                </div>
            </header>
            <div>
                <BonusBanner/>
                <div>
                    <div>
                        <div>
                            <label>{text.owedAmount}</label>
                        </div>
                        <div>
                            {account.balance}
                        </div>
                    </div>
                    <div>
                        <EarningsMessage />
                    </div>
                </div>
            </div>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="jobtype">{text.chooseJob}</label>
                        <div>
                            <Jobtypes id="jobtype" value={performedjob.transactionTypeId} jobtypes={jobtypes} onJobtypeFieldChange={onJobtypeFieldChange} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.amount}</label>
                        <div>
                            <input id="amount" type="text" value={performedjob.transactionAmount} readOnly={true} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="date">{text.date}</label>
                        <div>
                            <DatePicker selected={performedjob.transactionDate} dateFormat="YYYY-MM-DD" onChange={(selectedValue) => onDateFieldChange(selectedValue, performedjob)} readOnly={true} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => onRegisterJob(performedjob)}>{text.registerJob}</button>
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
            </div>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
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
