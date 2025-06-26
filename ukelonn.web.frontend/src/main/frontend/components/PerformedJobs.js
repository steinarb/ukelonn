import React from 'react';
import { useSelector } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    useGetAccountQuery,
    useGetJobsQuery,
} from '../api';
import { Link, useSearchParams } from 'react-router';
import Locale from './Locale';
import Logout from './Logout';

export default function PerformedJobs() {
    const [ queryParams ] = useSearchParams();
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const username = queryParams.get('username');
    const { data: account = {} } = useGetAccountQuery(username);
    const { firstname: accountFirstname } = account;
    const accountId = queryParams.get('accountId');
    const { data: jobs = [] } = useGetJobsQuery(accountId);
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
        </div>
    );
}
