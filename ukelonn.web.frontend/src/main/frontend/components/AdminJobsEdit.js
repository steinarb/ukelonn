import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import moment from 'moment';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    ACCOUNTS_REQUEST,
    JOBTYPELIST_REQUEST,
    RECENTJOBS_REQUEST,
    UPDATE,
    UPDATE_JOB_REQUEST,
} from '../actiontypes';
import Accounts from './Accounts';
import Jobtypes from './Jobtypes';

function reloadJobListWhenAccountHasChanged(oldAccount, newAccount, loadJobs) {
    if (oldAccount !== newAccount) {
        loadJobs(newAccount);
    }
}

class AdminJobsEdit extends Component {
    componentDidMount() {
        this.props.onAccounts();
        this.props.onJobtypeList();
        this.props.onJobs(this.props.account);
    }

    componentWillReceiveProps(props) {
        reloadJobListWhenAccountHasChanged(this.props.account, props.account, this.props.onJobs);

        this.setState({...props});
    }

    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let { account, jobs, accounts, accountsMap, jobtypes, jobtypesMap, selectedjob, onLogout, onJobtypeFieldChange, onAccountsFieldChange, onRowClick, onDateFieldChange, onSaveEditedJob } = this.props;

        return (
            <div>
                <Link to="/ukelonn/admin/jobtypes">Administer jobber og jobbtyper</Link>
                <br/>
                <h1>Endre jobber for {account.firstName}</h1>

                <label htmlFor="account-selector">Velg konto:</label>
                <Accounts  id="account-selector" accounts={accounts} accountsMap={accountsMap} account={account} onAccountsFieldChange={onAccountsFieldChange}/>
                <br/>

                <table className="table table-bordered">
                    <thead>
                        <tr>
                            <td>Dato</td>
                            <td>Jobber</td>
                            <td>Beløp</td>
                        </tr>
                    </thead>
                    <tbody>
                        {jobs.map((job) =>
                            <tr onClick={ ()=>onRowClick(account, job) } key={job.id}>
                                <td>{moment(job.transactionTime).format("YYYY-MM-DD")}</td>
                                <td>{job.name}</td>
                                <td>{job.transactionAmount}</td>
                            </tr>
                        )}
                    </tbody>
                </table>
                <h2>Endre jobb</h2>
                <label htmlFor="jobtype">Jobbtype</label>
                <Jobtypes id="jobtype" jobtypes={jobtypes} jobtypesMap={jobtypesMap} value={selectedjob.transactionType.transactionTypeName} account={account} performedjob={selectedjob} onJobtypeFieldChange={onJobtypeFieldChange} />
                <br/>
                <label htmlFor="amount">Beløp</label>
                <input id="amount" type="text" value={selectedjob.transactionAmount} readOnly="true" />
                <br/>
                <label htmlFor="date">Dato</label>
                <DatePicker selected={selectedjob.transactionTime} dateFormat="YYYY-MM-DD" onChange={(selectedValue) => onDateFieldChange(selectedValue, selectedjob)} readOnly={true} />
                <br/>
                <button onClick={() => onSaveEditedJob(selectedjob)}>Lagre endret jobb</button>
                <br/>
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
                <br/>
                <a href="../../../..">Tilbake til topp</a>
            </div>
        );
    }
};

function mapStateToProps(state) {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        account: state.account,
        jobs: state.jobs,
        accounts: state.accounts,
        accountsMap: state.accountsMap,
        jobtypes: state.jobtypes,
        jobtypesMap: state.jobtypesMap,
        selectedjob: state.selectedjob,
    };
}

const emptyJob = {
    accountId: -1,
    transactionType: { transactionTypeName: '' },
    transactionTypeId: -1,
    transactionAmount: 0.0,
    transactionTime: moment(),
};

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
        onAccounts: () => dispatch(ACCOUNTS_REQUEST()),
        onJobtypeList: () => dispatch(JOBTYPELIST_REQUEST()),
        onJobs: (account) => dispatch(RECENTJOBS_REQUEST(account.accountId)),
        onAccountsFieldChange: (selectedValue, accountsMap, paymenttype) => {
            let account = accountsMap.get(selectedValue);
            let changedField = {
                account,
                selectedjob: { ...emptyJob },
            };
            dispatch(UPDATE(changedField));
        },
        onRowClick: (account, job) => {
            const jobtype = job.transactionType;
            let changedField = {
                selectedjob: {
                    ...job,
                    accountId: account.accountId,
                    transactionTypeId: jobtype.id,
                    transactionTime: moment(job.transactionTime),
                },
            };
            dispatch(UPDATE(changedField));
        },
        onJobtypeFieldChange: (selectedValue, jobtypesMap, account, selectedjob) => {
            let jobtype = jobtypesMap.get(selectedValue);
            let changedField = {
                selectedjob: {
                    ...selectedjob,
                    transactionType: jobtype,
                    transactionTypeId: jobtype.id,
                    transactionAmount: jobtype.transactionAmount,
                }
            };
            dispatch(UPDATE(changedField));
        },
        onDateFieldChange: (selectedValue, selectedjob) => {
            let changedField = {
                selectedjob: {
                    ...selectedjob,
                    transactionTime: selectedValue,
                }
            };
            dispatch(UPDATE(changedField));
        },
        onSaveEditedJob: (selectedjob) => {
            dispatch(UPDATE_JOB_REQUEST({ selectedjob }));
            let changedField = {
                selectedjob: {
                    ...emptyJob,
                    transactionType: { transactionTypeName: '' },
                }
            };
            dispatch(UPDATE(changedField));
        },
    };
}

AdminJobsEdit = connect(mapStateToProps, mapDispatchToProps)(AdminJobsEdit);

export default AdminJobsEdit;
