import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useSwipeable } from 'react-swipeable';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    useGetJobsInfiniteQuery,
    usePostJobsDeleteMutation,
} from '../api';
import { Link } from 'react-router';
import { MODIFY_MARK_JOB_FOR_DELETE } from '../actiontypes';
import Locale from './Locale';
import Accounts from './Accounts';
import Logout from './Logout';

export default function AdminJobsDelete() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const account = useSelector(state => state.account);
    const { data: jobs, isSuccess: jobsIsSuccess, fetchNextPage } = useGetJobsInfiniteQuery(account.accountId);
    const jobIds = useSelector(state => state.jobIdsSelectedForDelete);
    const dispatch = useDispatch();
    const [ postJobsDelete ] = usePostJobsDeleteMutation();
    const onDeleteSelectedClicked = async () => await postJobsDelete({account, jobIds });
    const onNextPageClicked = async () => fetchNextPage();
    const swipeHandlers = useSwipeable({
        onSwipedUp: async () => fetchNextPage(),
    });

    return (
        <div {...swipeHandlers}>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/admin/jobtypes">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.deleteErronouslyRegisteredJobsFor} {account.firstname}</h1>
                <Locale />
            </nav>

            <p><em>{text.note}</em> {text.onlyMisregistrationsShouldBeDeleted}
                <br/>
            <em>{text.doNot}</em> {text.deleteJobsThatAreToBePaidFor}</p>

            <div className="container">
                <div className="form-group row mb-2">
                    <label htmlFor="account-selector" className="col-form-label col-5">{text.chooseAccount}:</label>
                    <div className="col-7">
                        <Accounts id="account-selector" />
                    </div>
                </div>
            </div>

            <button
                onClick={onDeleteSelectedClicked}>
                {text.deleteMarkedJobs}
            </button>

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
                        {jobsIsSuccess && jobs.pages.map((page) => page.map((job) =>
                            <tr key={job.id}>
                                <td><input
                                        type="checkbox"
                                        checked={jobIds.includes(job.id)}
                                        onChange={e => dispatch(MODIFY_MARK_JOB_FOR_DELETE({ id: job.id, delete: e.target.checked }))}/></td>
                                <td>{new Date(job.transactionTime).toISOString().split('T')[0]}</td>
                                <td>{job.name}</td>
                                <td>{job.transactionAmount}</td>
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
