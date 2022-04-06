import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import moment from 'moment';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    UPDATE_ACCOUNT,
    UPDATE_SELECTEDJOB,
    UPDATE_JOB_REQUEST,
    RECENTJOBS_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import Accounts from './Accounts';
import Jobtypes from './Jobtypes';

function AdminJobsEdit(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, account, jobs, accounts, jobtypes, selectedjob, onLogout, onJobtypeFieldChange, onAccountsFieldChange, onRowClick, onDateFieldChange, onSaveEditedJob } = props;

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/jobtypes">
                    &lt;-
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.modifyJobsFor} {account.firstName}</h1>
                <Locale />
            </nav>
            <div>
                <label htmlFor="account-selector">{text.chooseAccount}:</label>
                <Accounts  id="account-selector" value={account.accountId} accounts={accounts} onAccountsFieldChange={onAccountsFieldChange}/>
                <br/>

                <table className="table table-bordered">
                    <thead>
                        <tr>
                            <td>{text.date}</td>
                            <td>{text.jobs}</td>
                            <td>{text.amount}</td>
                        </tr>
                    </thead>
                    <tbody>
                        {jobs.map((job) =>
                                  <tr onClick={ ()=>onRowClick(job) } key={job.id}>
                                      <td>{moment(job.transactionTime).format("YYYY-MM-DD")}</td>
                                      <td>{job.name}</td>
                                      <td>{job.transactionAmount}</td>
                                  </tr>
                                 )}
                    </tbody>
                </table>
                <h2>{text.modifyJob}</h2>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div>
                        <div>
                            <label htmlFor="jobtype">{text.jobType}</label>
                            <div>
                                <Jobtypes id="jobtype" value={selectedjob.transactionTypeId} jobtypes={jobtypes} onJobtypeFieldChange={onJobtypeFieldChange} />
                            </div>
                        </div>
                        <div>
                            <label htmlFor="amount">{text.amount}</label>
                            <div>
                                <input id="amount" type="text" value={selectedjob.transactionAmount} readOnly={true} />
                            </div>
                        </div>
                        <div>
                            <label htmlFor="date">{text.date}</label>
                            <div>
                                <DatePicker selected={selectedjob.transactionTime.toDate()} dateFormat="yyyy-MM-dd" onChange={(selectedValue) => onDateFieldChange(selectedValue, selectedjob)} onFocus={e => e.target.blur()} />
                            </div>
                        </div>
                        <div>
                            <div/>
                            <div>
                                <button onClick={() => onSaveEditedJob(selectedjob)}>{text.saveChangesToJobType}</button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
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
        onDateFieldChange: (selectedValue) => dispatch(UPDATE_SELECTEDJOB({ transactionTime: moment(selectedValue) })),
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
