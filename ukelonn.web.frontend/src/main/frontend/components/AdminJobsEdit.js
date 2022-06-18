import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import {
    JOB_TABLE_ROW_CLICK,
    MODIFY_JOB_DATE,
    SAVE_CHANGES_TO_JOB_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import Accounts from './Accounts';
import Jobtypes from './Jobtypes';
import Logout from './Logout';

export default function AdminJobsEdit() {
    const text = useSelector(state => state.displayTexts);
    const firstname = useSelector(state => state.accountFirstname);
    const transactionAmount = useSelector(state => state.transactionAmount);
    const transactionDate = useSelector(state => state.transactionDate.split('T')[0]);
    const jobs = useSelector(state => state.jobs);
    const dispatch = useDispatch();

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
                            <tr onClick={ ()=>dispatch(JOB_TABLE_ROW_CLICK({ ...job })) } key={job.id}>
                                <td>{new Date(job.transactionTime).toISOString().split('T')[0]}</td>
                                <td>{job.name}</td>
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
                        <input
                            id="date"
                            className="form-control"
                            type="date"
                            value={transactionDate}
                            onChange={e => dispatch(MODIFY_JOB_DATE(e.target.value))}
                        />
                    </div>
                </div>
            </div>
            <button
                className="btn btn-primary"
                onClick={() => dispatch(SAVE_CHANGES_TO_JOB_BUTTON_CLICKED())}>
                {text.saveChangesToJob}
            </button>
            <br/>
            <br/>
            <Logout />
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}
