import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
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
    const { data: jobs, isSuccess: jobsIsSuccess } = useGetJobsInfiniteQuery(account.accountId);
    const jobIds = useSelector(state => state.jobIdsSelectedForDelete);
    const dispatch = useDispatch();
    const [ postJobsDelete ] = usePostJobsDeleteMutation();
    const onDeleteSelectedClicked = async () => await postJobsDelete({account, jobIds });

    return (
        <div>
            <nav>
                <Link to="/admin/jobtypes">
                    &lt;-
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.deleteErronouslyRegisteredJobsFor} {account.firstname}</h1>
                <Locale />
            </nav>

            <div>
                <p><em>{text.note}</em> {text.onlyMisregistrationsShouldBeDeleted}
                    <br/>
                    <em>{text.doNot}</em> {text.deleteJobsThatAreToBePaidFor}</p>

                <label htmlFor="account-selector">{text.chooseAccount}:</label>
                <Accounts id="account-selector" />
                <br/>
                <button
                    onClick={onDeleteSelectedClicked}>
                    {text.deleteMarkedJobs}
                </button>

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
            </div>
        </div>
    );
}
