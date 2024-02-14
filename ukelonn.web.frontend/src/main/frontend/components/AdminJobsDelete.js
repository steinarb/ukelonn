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
            <nav>
                <Link to="/admin/jobtypes">
                    &lt;-
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.deleteErronouslyRegisteredJobsFor} {accountFirstName}</h1>
                <Locale />
            </nav>

            <div>
                <p><em>{text.note}</em> {text.onlyMisregistrationsShouldBeDeleted}
                    <br/>
                    <em>{text.doNot}</em> {text.deleteJobsThatAreToBePaidFor}</p>

                <label htmlFor="account-selector">{text.chooseAccount}:</label>
                <Accounts id="account-selector" />

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
                <button
                    onClick={() => dispatch(DELETE_SELECTED_JOBS_BUTTON_CLICKED())}>
                    {text.deleteMarkedJobs}
                </button>
            </div>
            <br/>
            <br/>
            <Logout />
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}
