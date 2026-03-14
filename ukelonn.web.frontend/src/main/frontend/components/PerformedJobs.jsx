import React from 'react';
import { useSelector } from 'react-redux';
import { useSwipeable } from 'react-swipeable';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    useGetAccountQuery,
    useGetJobsInfiniteQuery,
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
    const { data: jobs, isSuccess: jobsIsSuccess, fetchNextPage } = useGetJobsInfiniteQuery(accountId);
    const parentTitle = queryParams.get('parentTitle');
    const onNextPageClicked = async () => fetchNextPage();
    const swipeHandlers = useSwipeable({
        onSwipedUp: async () => fetchNextPage(),
    });

    return (
        <div {...swipeHandlers}>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {parentTitle}
                </Link>
                <h1>{text.performedJobsFor} {accountFirstname}</h1>
                <Locale />
            </nav>
            <div className="table-responsive table-sm table-striped">
                <table className="table">
                    <thead>
                        <tr>
                            <th className="transaction-table-col1">{text.date}</th>
                            <th className="transaction-table-col-hide-overflow transaction-table-col2">{text.jobs}</th>
                            <th className="transaction-table-col-clip-overflow transaction-table-col3">{text.amount}</th>
                            <th className="transaction-table-col-clip-overflow transaction-table-col4">{text.paid}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {jobsIsSuccess && jobs.pages.map((page) => page.map((job) =>
                            <tr key={job.id}>
                                <td>{new Date(job.transactionTime).toISOString().split('T')[0]}</td>
                                <td>{job.name}</td>
                                <td>{job.transactionAmount}</td>
                                <td><input type="checkbox" checked={job.paidOut} readOnly={true}/></td>
                            </tr>
                        ))}
                    </tbody>
                </table>
                <div>
                    <button onClick={onNextPageClicked}>{text.next}</button>
                </div>
            </div>
        </div>
    );
}
