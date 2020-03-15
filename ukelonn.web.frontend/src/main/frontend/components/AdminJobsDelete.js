import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    RECENTJOBS_REQUEST,
    UPDATE_JOBS,
    UPDATE_ACCOUNT,
    DELETE_JOBS_REQUEST,
} from '../actiontypes';
import Accounts from './Accounts';

function reloadJobListWhenAccountHasChanged(oldAccount, newAccount, loadJobs) {
    if (oldAccount !== newAccount) {
        loadJobs(newAccount);
    }
}

function AdminJobsDelete(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { account, jobs, accounts, onLogout, onAccountsFieldChange, onCheckboxTicked, onDeleteMarkedJobs } = props;

    return (
        <div>
            <Link to="/ukelonn/admin/jobtypes">Administer jobber og jobbtyper</Link>
            <br/>
            <h1>Slett feilregisterte jobber for {account.firstName}</h1>

            <p><em>Merk!</em> Det er bare feilregistreringer som skal slettes!<br/>
               <em>Ikke</em> slett jobber som skal utbetales</p>
            <label htmlFor="account-selector">Velg konto:</label>
            <Accounts  id="account-selector" value={account.accountId} accounts={accounts} onAccountsFieldChange={onAccountsFieldChange}/>

            <table className="table table-bordered">
                <thead>
                    <tr>
                        <td>Slett</td>
                        <td>Dato</td>
                        <td>Jobber</td>
                        <td>Beløp</td>
                    </tr>
                </thead>
                <tbody>
                    {jobs.map((job) =>
                        <tr key={job.id}>
                            <td><input type="checkbox" checked={job.delete} onChange={(e) => onCheckboxTicked(e.target.checked, job, jobs)}/></td>
                            <td>{job.transactionTime}</td>
                            <td>{job.name}</td>
                            <td>{job.transactionAmount}</td>
                        </tr>
                    )}
                </tbody>
            </table>
            <button onClick={() => onDeleteMarkedJobs(account, jobs)}>Slett merkede jobber</button>
            <br/>
            <br/>
            <button onClick={() => onLogout()}>Logout</button>
            <br/>
            <a href="../../../..">Tilbake til topp</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        account: state.account,
        jobs: state.jobs,
        accounts: state.accounts,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
        onJobs: (account) => dispatch(RECENTJOBS_REQUEST(account.accountId)),
        onAccountsFieldChange: (selectedValue, accounts) => {
            const selectedValueInt = parseInt(selectedValue, 10);
            let account = accounts.find(account => account.accountId === selectedValueInt);
            dispatch(UPDATE_ACCOUNT(account));
            dispatch(RECENTJOBS_REQUEST(account.accountId));
        },
        onCheckboxTicked: (deleteChecked, job, origJobs) => {
            const jobs = origJobs.map(j => (j.id === job.id) ? { ...job, delete: deleteChecked } : j);
            dispatch(UPDATE_JOBS(jobs));
        },
        onDeleteMarkedJobs: (account, jobs) => {
            const jobsToDelete = jobs.filter(job => job.delete);
            dispatch(DELETE_JOBS_REQUEST({ account, jobsToDelete }));
        },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobsDelete);
