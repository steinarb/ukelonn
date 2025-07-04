import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useSwipeable } from 'react-swipeable';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    useGetJobsInfiniteQuery,
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
    const transaction = useSelector(state => state.transaction);
    const id = transaction.id;
    const transactionTypeId = transaction.transactionType.id;
    const transactionAmount = transaction.transactionAmount || '';
    const transactionTime = transaction.transactionTime;
    const transactionDateJustDate = transactionTime.split('T')[0];
    const { data: jobs, isSuccess: jobsIsSuccess, fetchNextPage } = useGetJobsInfiniteQuery(account.accountId);
    const dispatch = useDispatch();
    const [ postJobUpdate ] = usePostJobUpdateMutation();
    const onSaveEditToJobClicked = async () => await postJobUpdate({ accountId: account.accountId, id, transactionTypeId, transactionAmount, transactionTime });
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
                <h1>{text.modifyJobsFor} {account.firstname}</h1>
                <Locale />
            </nav>
            <div className="container">
                <div className="form-group row mb-2">
                    <label htmlFor="account-selector" className="col-form-label col-5">{text.chooseAccount}:</label>
                    <div className="col-7">
                        <Accounts id="account-selector" className="form-control" />
                    </div>
                </div>
            </div>
            <div className="container">
                <div className="form-group row mb-2">
                    <label htmlFor="jobtype" className="col-form-label col-5">{text.jobType}</label>
                    <div className="col-7">
                        <Jobtypes id="jobtype" />
                    </div>
                </div>
                <div className="form-group row mb-2">
                    <label htmlFor="amount" className="col-form-label col-5">{text.amount}</label>
                    <div className="col-7">
                        <input id="amount" className="form-control" type="text" value={transactionAmount} readOnly={true} />
                    </div>
                </div>
                <div className="form-group row mb-2">
                    <label htmlFor="date" className="col-form-label col-5">{text.date}</label>
                    <div className="col-7">
                        <input
                            id="date"
                            className="form-control"
                            type="date"
                            value={transactionDateJustDate}
                            onChange={e => dispatch(MODIFY_JOB_DATE(e.target.value))}
                        />
                    </div>
                </div>
            </div>
            <button
                className="btn btn-primary"
                onClick={onSaveEditToJobClicked}>
                {text.saveChangesToJob}
            </button>

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
                        {jobsIsSuccess && jobs.pages.map((page) => page.map((job) =>
                            <tr onClick={ ()=>dispatch(JOB_TABLE_ROW_CLICK({ ...job })) } key={job.id}>
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
