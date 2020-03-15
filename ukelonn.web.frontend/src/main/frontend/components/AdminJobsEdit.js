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

    const reduceHeaderRowPadding = { padding: '0 0 0 0' };
    let { account, jobs, accounts, jobtypes, selectedjob, onLogout, onJobtypeFieldChange, onAccountsFieldChange, onRowClick, onDateFieldChange, onSaveEditedJob } = props;

    return (
        <div className="mdl-layout mdl-layout--fixed-header">
            <header className="mdl-layout__header">
                <div className="mdl-layout__header-row" style={reduceHeaderRowPadding}>
                    <Link to="/ukelonn/admin/jobtypes" className="mdl-navigation__link">
                        <i className="material-icons" >chevron_left</i>
                        &nbsp;
                        Administrer jobber og jobbtyper
                    </Link>
                    <span className="mdl-layout-title">Endre jobber for {account.firstName}</span>
                </div>
            </header>
            <main className="mdl-layout__content">

                <div className="mdl-grid hline-bottom">
                    <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                        <label htmlFor="account-selector">Velg konto:</label>
                    </div>
                    <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                        <Accounts id="account-selector" value={account.accountId} accounts={accounts} onAccountsFieldChange={onAccountsFieldChange}/>
                    </div>
                </div>

                <table className="mdl-data-table mdl-js-data-table transaction-table">
                    <thead>
                        <tr>
                            <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col1">Dato</td>
                            <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col-hide-overflow transaction-table-col2">Jobber</td>
                            <td className="transaction-table-col transaction-table-col3">Bel.</td>
                        </tr>
                    </thead>
                    <tbody>
                        {jobs.map((job) =>
                            <tr onClick={ ()=>onRowClick(account, job) } key={job.id}>
                                <td className="mdl-data-table__cell--non-numeric transaction-table-col">{moment(job.transactionTime).format("YYYY-MM-DD")}</td>
                                <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col-hide-overflow">{job.name}</td>
                                <td className="transaction-table-col">{job.transactionAmount}</td>
                            </tr>
                        )}
                    </tbody>
                </table>

                <h2>Endre jobb</h2>

                <div className="mdl-grid hline-bottom">
                    <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                        <label htmlFor="jobtype">Jobbtype</label>
                    </div>
                    <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                        <Jobtypes id="jobtype" value={selectedjob.transactionTypeId} jobtypes={jobtypes} onJobtypeFieldChange={onJobtypeFieldChange} />
                    </div>
                </div>

                <div className="mdl-grid hline-bottom">
                    <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                        <label htmlFor="amount">Beløp</label>
                     </div>
                     <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                        <input id="amount" type="text" className='mdl-textfield__input' value={selectedjob.transactionAmount} readOnly="true" />
                     </div>
                </div>

                <div className="mdl-grid hline-bottom">
                    <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                        <label htmlFor="date">Dato</label>
                    </div>
                    <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                        <DatePicker selected={selectedjob.transactionTime} dateFormat="YYYY-MM-DD" onChange={(selectedValue) => onDateFieldChange(selectedValue, selectedjob)} readOnly={true} />
                    </div>
                </div>
                <button onClick={() => onSaveEditedJob(selectedjob)}>Lagre endret jobb</button>
            </main>
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
