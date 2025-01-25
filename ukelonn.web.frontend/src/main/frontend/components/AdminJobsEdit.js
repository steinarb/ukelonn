import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    useGetJobsQuery,
    usePostJobUpdateMutation,
} from '../api';
import { Link } from 'react-router';
import { JOB_TABLE_ROW_CLICK, MODIFY_JOB_DATE } from '../actiontypes';
import Locale from './Locale';
import Accounts from './Accounts';
import Jobtypes from './Jobtypes';
import Logout from './Logout';

export default function AdminJobsEdit() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const account = useSelector(state => state.account);
    const id = useSelector(state => state.transactionId);
    const transactionTypeId = useSelector(state => state.transactionTypeId);
    const transactionAmount = useSelector(state => state.transactionAmount);
    const transactionTime = useSelector(state => state.transactionDate);
    const transactionDateJustDate = transactionTime.split('T')[0];
    const { data: jobs = [] } = useGetJobsQuery(account.accountId);
    const dispatch = useDispatch();
    const [ postJobUpdate ] = usePostJobUpdateMutation();
    const onSaveEditToJobClicked = async () => await postJobUpdate({ accountId: account.accountId, id, transactionTypeId, transactionAmount, transactionTime });

    return (
        <div>
            <nav>
                <Link to="/admin/jobtypes">
                    &lt;-
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.modifyJobsFor} {account.firstname}</h1>
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
                            <tr onClick={ ()=>dispatch(JOB_TABLE_ROW_CLICK({ ...job })) } key={job.id}>
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
                                <input
                                    id="date"
                                    type="date"
                                    value={transactionDateJustDate}
                                    onChange={e => dispatch(MODIFY_JOB_DATE(e.target.value))}
                                />
                            </div>
                        </div>
                        <div>
                            <div/>
                            <div>
                                <button
                                    onClick={onSaveEditToJobClicked}>
                                    {text.saveChangesToJob}
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <br/>
            <br/>
            <Logout />
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}
