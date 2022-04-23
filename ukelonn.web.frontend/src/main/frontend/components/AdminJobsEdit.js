import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import {
    JOB_TABLE_ROW_CLICK,
    MODIFY_JOB_DATE,
    UPDATE_JOB_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import Accounts from './Accounts';
import Jobtypes from './Jobtypes';
import Logout from './Logout';

export default function AdminJobsEdit() {
    const text = useSelector(state => state.displayTexts);
    const accountId = useSelector(state => state.accountId);
    const firstname = useSelector(state => state.accountFirstname);
    const transactionId = useSelector(state => state.transactionId);
    const transactionTypeId = useSelector(state => state.transactionTypeId);
    const transactionAmount = useSelector(state => state.transactionAmount);
    const transactionTime = useSelector(state => state.transactionDate);
    const jobs = useSelector(state => state.jobs);
    const dispatch = useDispatch();

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/jobtypes">
                    &lt;-
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.modifyJobsFor} {firstname}</h1>
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
                                <DatePicker
                                    selected={new Date(transactionTime)}
                                    dateFormat="yyyy-MM-dd"
                                    onChange={d => dispatch(MODIFY_JOB_DATE(d))}
                                    onFocus={e => e.target.blur()} />
                            </div>
                        </div>
                        <div>
                            <div/>
                            <div>
                                <button onClick={() => dispatch(UPDATE_JOB_REQUEST({ id: transactionId, accountId, transactionTypeId, transactionAmount, transactionTime }))}>{text.saveChangesToJob}</button>
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
