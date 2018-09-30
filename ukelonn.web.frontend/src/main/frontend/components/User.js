import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import moment from 'moment';
import Jobtypes from './Jobtypes';
import Notification from './Notification';

class User extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onAccount(this.props.loginResponse.username);
        this.props.onNotifyStart(this.props.loginResponse.username);
        this.props.onJobtypeList();
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let { loginResponse, account, jobtypes, jobtypesMap, performedjob, notificationMessage, onJobtypeFieldChange, onDateFieldChange, onRegisterJob, onLogout } = this.state;
        if (loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        const performedjobs = "/ukelonn/performedjobs?" + stringify({ accountId: account.accountId, username: account.username });
        const performedpayments = "/ukelonn/performedpayments?" + stringify({ accountId: account.accountId, username: account.username });

        return (
            <div>
                <Notification notificationMessage={notificationMessage}/>
                <h1>Ukelønn for {account.firstName}</h1>
                <div>Til gode: { account.balance }</div><br/>
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
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
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
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
        onAccount: (username) => dispatch({ type: 'ACCOUNT_REQUEST', username }),
        onNotifyStart: (username) => dispatch({ type: 'START_NOTIFICATION_LISTENING', username }),
        onJobtypeList: () => dispatch({ type: 'JOBTYPELIST_REQUEST' }),
        onJobtypeFieldChange: (selectedValue, jobtypesMap, account, performedjob) => {
            let jobtype = jobtypesMap.get(selectedValue);
            let changedField = {
                performedjob: {
                    ...performedjob,
                    transactionTypeId: jobtype.id,
                    transactionName: jobtype.transactionName,
                    transactionAmount: jobtype.transactionAmount,
                    account: account
                }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onDateFieldChange: (selectedValue, performedjob) => {
            let changedField = {
                performedjob: {
                    ...performedjob,
                    transactionDate: selectedValue,
                }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onRegisterJob: (performedjob) => dispatch({ type: 'REGISTERJOB_REQUEST', performedjob: performedjob }),
    };
};

User = connect(mapStateToProps, mapDispatchToProps)(User);

export default User;
