import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import {
    JOB_TABLE_ROW_CLICK,
    MODIFY_JOB_DATE,
    UPDATE_JOB_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import Accounts from './Accounts';
import Jobtypes from './Jobtypes';
import Logout from './Logout';

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
        onRowClick,
        onDateFieldChange,
        onSaveEditedJob,
    } = props;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/admin/jobtypes">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.modifyJobsFor} {firstname}</h1>
                <Locale />
            </nav>


            <div className="container">
                <div className="form-group row">
                    <label htmlFor="account-selector" className="col-form-label col-5">{text.chooseAccount}:</label>
                    <div className="col-7">
                        <Accounts id="account-selector" className="form-control" />
                    </div>
                </div>
            </div>

            <div className="table-responsive table-sm table-striped">
                <table className="table">
                    <thead>
                        <tr>
                            <th className="transaction-table-col1">{text.date}Dato</th>
                            <th className="transaction-table-col-hide-overflow transaction-table-col2">{text.jobs}</th>
                            <th className="transaction-table-col3">{text.amount}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {jobs.map((job) =>
                            <tr onClick={ ()=>onRowClick(job) } key={job.id}>
                                <td>{new Date(job.transactionTime).toISOString().split('T')[0]}</td>
                                <td className="transaction-table-col-hide-overflow">{job.name}</td>
                                <td>{job.transactionAmount}</td>
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
                        <Jobtypes id="jobtype" />
                    </div>
                </div>
                <div className="form-group row">
                    <label htmlFor="amount" className="col-form-label col-5">{text.amount}</label>
                    <div className="col-7">
                        <input id="amount" type="text" value={transactionAmount} readOnly={true} />
                    </div>
                </div>
                <div className="form-group row">
                    <label htmlFor="date" className="col-form-label col-5">{text.date}</label>
                    <div className="col-7">
                        <DatePicker selected={new Date(transactionTime)} dateFormat="yyyy-MM-dd" onChange={(selectedValue) => onDateFieldChange(selectedValue)} onFocus={e => e.target.blur()} />
                    </div>
                </div>
            </div>
            <button onClick={() => onSaveEditedJob({ id: transactionId, accountId, transactionTypeId, transactionAmount, transactionTime })}>{text.saveChangesToJob}</button>
            <br/>
            <br/>
            <Logout />
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
        onRowClick: (job) => dispatch(JOB_TABLE_ROW_CLICK({ ...job })),
        onDateFieldChange: (selectedValue) => dispatch(MODIFY_JOB_DATE(selectedValue)),
        onSaveEditedJob: (modifiedJob) => dispatch(UPDATE_JOB_REQUEST(modifiedJob)),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobsEdit);
