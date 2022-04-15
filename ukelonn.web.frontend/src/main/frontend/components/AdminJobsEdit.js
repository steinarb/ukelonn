import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    JOB_TABLE_ROW_CLICK,
    MODIFY_JOB_DATE,
    UPDATE_JOB_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import Accounts from './Accounts';
import Jobtypes from './Jobtypes';

function AdminJobsEdit(props) {
    const {
        text,
        accountId,
        firstname,
        transactionId,
        transactionTypeId,
        transactionAmount,
        transactionTime,
        jobs,
        onLogout,
        onRowClick,
        onDateFieldChange,
        onSaveEditedJob,
    } = props;

    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/jobtypes">
                    &lt;-
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.modifyJobsFor} {firstname}</h1>
                <Locale />
            </nav>
            <div>
                <label htmlFor="account-selector">{text.chooseAccount}:</label>
                <Accounts id="account-selector" />
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
                                      <td>{new Date(job.transactionTime).toISOString().split('T')[0]}</td>
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
                                <Jobtypes id="jobtype" />
                            </div>
                        </div>
                        <div>
                            <label htmlFor="amount">{text.amount}</label>
                            <div>
                                <input id="amount" type="text" value={transactionAmount} readOnly={true} />
                            </div>
                        </div>
                        <div>
                            <label htmlFor="date">{text.date}</label>
                            <div>
                                <DatePicker selected={new Date(transactionTime)} dateFormat="yyyy-MM-dd" onChange={(selectedValue) => onDateFieldChange(selectedValue)} onFocus={e => e.target.blur()} />
                            </div>
                        </div>
                        <div>
                            <div/>
                            <div>
                                <button onClick={() => onSaveEditedJob({ id: transactionId, accountId, transactionTypeId, transactionAmount, transactionTime })}>{text.saveChangesToJob}</button>
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
        accountId: state.accountId,
        firstname: state.accountFirstname,
        transactionId: state.transactionId,
        transactionTypeId: state.transactionTypeId,
        transactionAmount: state.transactionAmount,
        transactionTime: state.transactionDate,
        jobs: state.jobs,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
        onRowClick: (job) => dispatch(JOB_TABLE_ROW_CLICK({ ...job })),
        onDateFieldChange: (selectedValue) => dispatch(MODIFY_JOB_DATE(selectedValue)),
        onSaveEditedJob: (modifiedJob) => dispatch(UPDATE_JOB_REQUEST(modifiedJob)),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobsEdit);
