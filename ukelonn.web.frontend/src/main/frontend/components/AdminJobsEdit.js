import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import moment from 'moment';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    UPDATE_JOBS,
    UPDATE_ACCOUNT,
    UPDATE_SELECTEDJOB,
    UPDATE_JOB_REQUEST,
    RECENTJOBS_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import Accounts from './Accounts';
import Jobtypes from './Jobtypes';

function reloadJobListWhenAccountHasChanged(oldAccount, newAccount, loadJobs) {
    if (oldAccount !== newAccount) {
        loadJobs(newAccount);
    }
}

function AdminJobsEdit(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, account, jobs, accounts, jobtypes, selectedjob, onLogout, onJobtypeFieldChange, onAccountsFieldChange, onRowClick, onDateFieldChange, onSaveEditedJob } = props;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/admin/jobtypes">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.modifyJobsFor} {account.firstName}</h1>
                <Locale />
            </nav>


            <div className="container">
                <div className="form-group row">
                    <label htmlFor="account-selector" className="col-form-label col-5">{text.chooseAccount}:</label>
                    <div className="col-7">
                        <Accounts  id="account-selector" className="form-control" value={account.accountId} accounts={accounts} onAccountsFieldChange={onAccountsFieldChange}/>
                    </div>
                </div>
            </div>

            <div className="table-responsive table-sm table-striped">
                <table className="table">
                    <thead>
                        <tr>
                            <th className="transaction-table-col transaction-table-col1">{text.date}Dato</th>
                            <th className="transaction-table-col transaction-table-col-hide-overflow transaction-table-col2">{text.jobs}</th>
                            <th className="transaction-table-col transaction-table-col3">{text.amount}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {jobs.map((job) =>
                            <tr onClick={ ()=>onRowClick(account, job) } key={job.id}>
                                 <td className="transaction-table-col">{moment(job.transactionTime).format("YYYY-MM-DD")}</td>
                                 <td className="transaction-table-col transaction-table-col-hide-overflow">{job.name}</td>
                                 <td className="transaction-table-col">{job.transactionAmount}</td>
                            </tr>
                        )}
                     </tbody>
                </table>
            </div>
            <h2>Endre jobb</h2>
            <div className="container">
                <div className="form-group row">
                    <label htmlFor="jobtype" className="col-form-label col-5">{text.jobType}</label>
                    <div className="col-7">
                        <Jobtypes id="jobtype" value={selectedjob.transactionTypeId} jobtypes={jobtypes} onJobtypeFieldChange={onJobtypeFieldChange} />
                    </div>
                </div>
                <div className="form-group row">
                    <label htmlFor="amount" className="col-form-label col-5">{text.amount}</label>
                    <div className="col-7">
                        <input id="amount" type="text" value={selectedjob.transactionAmount} readOnly={true} />
                    </div>
                </div>
                <div className="form-group row">
                    <label htmlFor="date" className="col-form-label col-5">{text.date}</label>
                    <div className="col-7">
                        <DatePicker id="date" selected={selectedjob.transactionTime} dateFormat="YYYY-MM-DD" onChange={(selectedValue) => onDateFieldChange(selectedValue, selectedjob)} readOnly={true} />
                    </div>
                </div>
            </div>
            <button onClick={() => onSaveEditedJob(selectedjob)}>{text.saveChangesToJobType}</button>
            <br/>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        account: state.account,
        jobs: state.jobs,
        accounts: state.accounts,
        jobtypes: state.jobtypes,
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
        onAccountsFieldChange: (selectedValue, accounts) => {
            const selectedValueInt = parseInt(selectedValue, 10);
            let account = accounts.find(account => account.accountId === selectedValueInt);
            dispatch(UPDATE_ACCOUNT(account));
            dispatch(RECENTJOBS_REQUEST(account.accountId));
        },
        onRowClick: (job) => dispatch(UPDATE_SELECTEDJOB({ ...job, transactionTypeId: job.transactionType.id, transactionTime: moment(job.transactionTime) })),
        onJobtypeFieldChange: (selectedValue, jobtypes) => {
            const selectedValueInt = parseInt(selectedValue, 10);
            const jobtype = jobtypes.find(j => j.id === selectedValueInt);
            const { id: transactionTypeId, transactionAmount } = jobtype;
            dispatch(UPDATE_SELECTEDJOB({ transactionTypeId, transactionAmount }));
        },
        onDateFieldChange: (selectedValue) => dispatch(UPDATE_SELECTEDJOB({ transactionTime: selectedValue })),
        onSaveEditedJob: (selectedjob) => {
            dispatch(UPDATE_JOB_REQUEST({ selectedjob }));
            let changedField = {
                selectedjob: {
                    ...emptyJob,
                    transactionType: { transactionTypeName: '' },
                }
            };
            dispatch(UPDATE_SELECTEDJOB(changedField));
        },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobsEdit);
