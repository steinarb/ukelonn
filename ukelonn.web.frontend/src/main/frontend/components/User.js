import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import {
    MODIFY_JOB_DATE,
    REGISTER_JOB_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import BonusBanner from './BonusBanner';
import Jobtypes from './Jobtypes';
import Notification from './Notification';
import EarningsMessage from './EarningsMessage';
import Logout from './Logout';

export default function User() {
    const text = useSelector(state => state.displayTexts);
    const accountId = useSelector(state => state.accountId);
    const firstname = useSelector(state => state.accountFirstname);
    const username = useSelector(state => state.accountUsername);
    const accountBalance = useSelector(state => state.accountBalance);
    const transactionAmount = useSelector(state => state.transactionAmount);
    const transactionDate = useSelector(state => state.transactionDate);
    const notificationMessage = useSelector(state => state.notificationMessage);
    const dispatch = useDispatch();
    const title = text.weeklyAllowanceFor + ' ' + firstname;
    const performedjobs = '/ukelonn/performedjobs?' + stringify({ accountId, username, parentTitle: title });
    const performedpayments = '/ukelonn/performedpayments?' + stringify({ accountId, username, parentTitle: title });
    const statistics = '/ukelonn/statistics?' + stringify({ username });

    return (
        <div>
            <Notification notificationMessage={notificationMessage}/>
            <nav>
                <a href="../..">&lt;-&nbsp;{text.returnToTop}</a>
                <h1 id="logo">{title}</h1>
                <Locale />
            </nav>
            <div className="container-fluid">
                <BonusBanner/>
                <div>
                    <div>
                        <div>
                            <label>{text.owedAmount}</label>
                        </div>
                        <div>
                            {accountBalance}
                        </div>
                    </div>
                    <div>
                        <EarningsMessage />
                    </div>
                </div>
            </div>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="jobtype">{text.chooseJob}</label>
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
                                selected={new Date(transactionDate)}
                                dateFormat="yyyy-MM-dd"
                                onChange={d => dispatch(MODIFY_JOB_DATE(d))}
                                onFocus={e => e.target.blur()} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => dispatch(REGISTER_JOB_BUTTON_CLICKED())}>{text.registerJob}</button>
                        </div>
                    </div>
                </div>
            </form>
            <div>
                <Link to={performedjobs}>
                    {text.performedJobs}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to={performedpayments}>
                    {text.performedPayments}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to={statistics}>
                    {text.statistics}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
            </div>
            <br/>
            <Logout />
            <br/>
        </div>
    );
}
