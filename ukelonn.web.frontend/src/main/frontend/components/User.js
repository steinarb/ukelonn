import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import moment from 'moment';
import Jobtypes from './Jobtypes';

class User extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onAccount(this.props.loginResponse.username);
        this.props.onJobtypeList();
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let { loginResponse, account, jobtypes, jobtypesMap, performedjob, onJobtypeFieldChange, onDateFieldChange, onRegisterJob, onLogout } = this.state;
        if (loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        const performedjobs = "/ukelonn/performedjobs?" + stringify({ accountId: account.accountId, username: account.username });
        const performedpayments = "/ukelonn/performedpayments?" + stringify({ accountId: account.accountId, username: account.username });

        return (
            <div>
                <header>
                    <div className="pb-2 mt-4 mb-2 border-bottom bg-light">
                        <h1 id="logo">Ukelønn for {account.firstName}</h1>
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
    if (!state.jobtypes.find((job) => job.id === -1)) {
        state.jobtypes.unshift(emptyJob);
    }

    return {
        loginResponse: state.loginResponse,
        account: state.account,
        jobtypes: state.jobtypes,
        jobtypesMap: new Map(state.jobtypes.map(i => [i.transactionTypeName, i])),
        performedjob: state.performedjob,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
        onAccount: (username) => dispatch({ type: 'ACCOUNT_REQUEST', username }),
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
