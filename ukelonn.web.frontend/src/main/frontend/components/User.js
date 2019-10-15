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
    ACCOUNT_REQUEST,
    START_NOTIFICATION_LISTENING,
    JOBTYPELIST_REQUEST,
    UPDATE,
    REGISTERJOB_REQUEST,
} from '../actiontypes';
import Jobtypes from './Jobtypes';
import Notification from './Notification';
import EarningsMessage from './EarningsMessage';

class User extends Component {
    componentDidMount() {
        this.props.onAccount(this.props.loginResponse.username);
        this.props.onNotifyStart(this.props.loginResponse.username);
        this.props.onJobtypeList();
    }

    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let { account, jobtypes, jobtypesMap, performedjob, notificationMessage, onJobtypeFieldChange, onDateFieldChange, onRegisterJob, onLogout } = this.props;
        const title = 'Ukelønn for ' + account.firstName;
        const username = account.username;
        const performedjobs = '/ukelonn/performedjobs?' + stringify({ accountId: account.accountId, username, parentTitle: title });
        const performedpayments = '/ukelonn/performedpayments?' + stringify({ accountId: account.accountId, username, parentTitle: title });
        const statistics = '/ukelonn/statistics?' + stringify({ username });

        return (
            <div className="mdl-layout mdl-layout--fixed-header">
                <Notification notificationMessage={notificationMessage}/>
                <header className="mdl-layout__header">
                    <div className="mdl-layout__header-row">
                        <span className="mdl-layout-title">{title}</span>
                        <div className="mdl-layout-spacer"></div>
                    </div>
                </header>
                <main className="mdl-layout__content">
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="jobtype">Til gode:</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            { account.balance }
                        </div>
                    </div>
                    <form onSubmit={ e => { e.preventDefault(); }}>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                                <label htmlFor="jobtype">Velg jobb</label>
                            </div>
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                                <Jobtypes id="jobtype" className="stretch-to-fill" jobtypes={jobtypes} jobtypesMap={jobtypesMap} value={performedjob.transactionName} account={account} performedjob={performedjob} onJobtypeFieldChange={onJobtypeFieldChange} />
                            </div>
                        </div>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                                <label htmlFor="amount">Beløp</label>
                            </div>
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                                <input id="amount" className='mdl-textfield__input stretch-to-fill' type="text" value={performedjob.transactionAmount} readOnly="true" /><br/>
                            </div>
                        </div>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                                <label htmlFor="date">Dato</label>
                            </div>
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                                <DatePicker selected={performedjob.transactionDate} dateFormat="YYYY-MM-DD" onChange={(selectedValue) => onDateFieldChange(selectedValue, performedjob)} readOnly={true} />
                            </div>
                        </div>
                        <div className="mdl-grid mdl-grid--no-spacing hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--6-col-tablet mdl-cell--10-col-desktop">
                                &nbsp;
                            </div>
                            <div className="mdl-cell mdl-cell--2-col">
                                <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onRegisterJob(performedjob)}>Registrer jobb</button>
                            </div>
                        </div>
                    </form>
                    <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to={performedjobs}>
                        Siste jobber
                        <i className="material-icons">chevron_right</i>
                    </Link>
                    <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to={performedpayments}>
                        Siste utbetalinger
                        <i className="material-icons">chevron_right</i>
                    </Link>
                </main>
                <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onLogout()}>Logout</button>
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
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        account: state.account,
        jobtypes: state.jobtypes,
        jobtypesMap: state.jobtypesMap,
        performedjob: state.performedjob,
        notificationMessage: state.notificationMessage,
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
