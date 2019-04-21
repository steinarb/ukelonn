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
        let { loginResponse, account, jobtypes, jobtypesMap, performedjob, notificationMessage, onJobtypeFieldChange, onDateFieldChange, onRegisterJob, onLogout } = this.props;
        if (loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        const title = 'Ukelønn for ' + account.firstName;
        const performedjobs = "/ukelonn/performedjobs?" + stringify({ accountId: account.accountId, username: account.username, parentTitle: title });
        const performedpayments = "/ukelonn/performedpayments?" + stringify({ accountId: account.accountId, username: account.username, parentTitle: title });

        return (
            <div>
                <Notification notificationMessage={notificationMessage}/>
                <header>
                    <div className="pb-2 mt-4 mb-2 border-bottom bg-light">
                        <h1 id="logo">{title}</h1>
                    </div>
                </header>
                <div className="container-fluid">
                    <div className="container">
                        <div className="row border rounded mb-3">
                            <div className="col">
                                <label>Til gode:</label>
                            </div>
                            <div className="col">
                                { account.balance }
                            </div>
                        </div>
                    </div>
                    <form onSubmit={ e => { e.preventDefault(); }}>
                        <div className="container">
                            <div className="form-group row">
                                <label htmlFor="jobtype" className="col-form-label col-5">Velg jobb</label>
                                <div className="col-7">
                                    <Jobtypes id="jobtype" className="form-control" jobtypes={jobtypes} jobtypesMap={jobtypesMap} value={performedjob.transactionName} account={account} performedjob={performedjob} onJobtypeFieldChange={onJobtypeFieldChange} />
                                </div>
                            </div>
                            <div className="form-group row">
                                <label htmlFor="amount" className="col-form-label col-5">Beløp</label>
                                <div className="col-7">
                                    <input id="amount" className="form-control" type="text" value={performedjob.transactionAmount} readOnly="true" /><br/>
                                </div>
                            </div>
                            <div className="form-group row">
                                <label htmlFor="date" className="col-form-label col-5">Dato</label>
                                <div className="col-7">
                                    <DatePicker selected={performedjob.transactionDate} dateFormat="YYYY-MM-DD" onChange={(selectedValue) => onDateFieldChange(selectedValue, performedjob)} readOnly={true} />
                                </div>
                            </div>
                            <div className="form-group row">
                                <div className="col-5"/>
                                <div className="col-7">
                                    <button className="btn btn-primary" onClick={() => onRegisterJob(performedjob)}>Registrer jobb</button>
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
                    </div>
                    <br/>
                    <br/>
                    <button className="btn btn-default" onClick={() => onLogout()}>Logout</button>
                    <br/>
                    <a href="../..">Tilbake til topp</a>
                </div>
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
