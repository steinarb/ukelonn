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
                            { account.balance }
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
                                <Jobtypes id="jobtype" className="form-control" value={performedjob.transactionTypeId} jobtypes={jobtypes} onJobtypeFieldChange={onJobtypeFieldChange} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="amount" className="col-form-label col-5">{text.amount}</label>
                            <div className="col-7">
                                <input id="amount" className="form-control" type="text" value={performedjob.transactionAmount} readOnly={true} /><br/>
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="date" className="col-form-label col-5">{text.date}</label>
                            <div className="col-7">
                                <DatePicker selected={new Date(performedjob.transactionDate)} dateFormat="yyyy-MM-dd" onChange={(selectedValue) => onDateFieldChange(selectedValue, performedjob)} onFocus={e => e.target.blur()} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <div className="col-5"/>
                            <div className="col-7">
                                <button className="btn btn-primary" onClick={() => onRegisterJob(performedjob)}>{text.registerJob}</button>
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
                transactionDate: new Date().toISOString(),
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
