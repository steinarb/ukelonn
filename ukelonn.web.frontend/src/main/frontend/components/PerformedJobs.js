import React from 'react';
import { useSelector } from 'react-redux';
import { Link, useSearchParams } from 'react-router';
import Locale from './Locale';
import Logout from './Logout';

export default function PerformedJobs() {
    const text = useSelector(state => state.displayTexts);
    const accountFirstname = useSelector(state => state.accountFirstname);
    const jobs = useSelector(state => state.jobs);
    const [ queryParams ] = useSearchParams();
    const parentTitle = queryParams.get('parentTitle');

    return (
        <div>
            <nav>
                <Link to="/">
                    &lt;-
                    &nbsp;
                    {parentTitle}
                </Link>
                <h1>{text.performedJobsFor} {accountFirstname}</h1>
                <Locale />
            </nav>
            <div>
                <table className="table table-bordered">
                    <thead>
                        <tr>
                            <th>{text.date}</th>
                            <th>{text.jobs}</th>
                            <th>{text.amount}</th>
                            <th>{text.paid}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {jobs.map((job) =>
                                  <tr key={job.id}>
                                      <td>{new Date(job.transactionTime).toISOString().split('T')[0]}</td>
                                      <td>{job.name}</td>
                                      <td>{job.transactionAmount}</td>
                                      <td><input type="checkbox" checked={job.paidOut} readOnly={true}/></td>
                                  </tr>
                                 )}
                    </tbody>
                </table>
            </div>
            <br/>
            <br/>
            <Logout />
            <br/>
            <a href="../..">{text.returnToTop}</a>
        </div>
    );
}
