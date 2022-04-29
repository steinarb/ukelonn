import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import {
    MODIFY_MARK_JOB_FOR_DELETE,
    DELETE_SELECTED_JOBS_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import Accounts from './Accounts';
import Logout from './Logout';

export default function AdminJobsDelete() {
    const text = useSelector(state => state.displayTexts);
    const accountFirstName = useSelector(state => state.accountFirstName);
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
                <h1>{text.deleteErronouslyRegisteredJobsFor} {accountFirstName}</h1>
                <Locale />
            </nav>

            <p><em>{text.note}</em> {text.onlyMisregistrationsShouldBeDeleted}
                <br/>
                <em>{text.doNot}</em> {text.deleteJobsThatAreToBePaidFor}</p>

            <div className="container">
                <div className="form-group row">
                    <label htmlFor="account-selector" className="col-form-label col-5">{text.chooseAccount}:</label>
                    <div className="col-7">
                        <Accounts id="account-selector" />
                    </div>
                </div>
            </div>

            <div className="table-responsive table-sm table-striped">
                <table className="table table-bordered">
                    <thead>
                        <tr>
                            <td>{text.delete}</td>
                            <td>{text.date}</td>
                            <td>{text.jobs}</td>
                            <td>{text.amount}</td>
                        </tr>
                    </thead>
                    <tbody>
                        {jobs.map((job) =>
                                  <tr key={job.id}>
                                      <td><input
                                              type="checkbox"
                                              checked={job.delete}
                                              onChange={e => dispatch(MODIFY_MARK_JOB_FOR_DELETE({ ...job, delete: e.target.checked }))}/></td>
                                      <td>{new Date(job.transactionTime).toISOString().split('T')[0]}</td>
                                      <td>{job.name}</td>
                                      <td>{job.transactionAmount}</td>
                                  </tr>
                                 )}
                    </tbody>
                </table>
            </div>
            <button
                onClick={() => dispatch(DELETE_SELECTED_JOBS_BUTTON_CLICKED())}>
                {text.deleteMarkedJobs}
            </button>
            <br/>
            <br/>
            <Logout />
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}
