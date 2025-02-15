import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetLoginQuery,
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    useGetAccountQuery,
    useGetJobtypesQuery,
    usePostJobRegisterMutation,
} from '../api';
import { Link } from 'react-router';
import { stringify } from 'qs';
import { MODIFY_JOB_DATE } from '../actiontypes';
import Locale from './Locale';
import BonusBanner from './BonusBanner';
import Jobtypes from './Jobtypes';
import Notifier from './Notifier';
import EarningsMessage from './EarningsMessage';
import Logout from './Logout';

export default function User() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const { data: loginResponse = { username: '' }, isSuccess: loginIsSuccess } = useGetLoginQuery();
    const { data: account = {} } = useGetAccountQuery(loginResponse.username, { skip: !loginIsSuccess });
    const { accountId, firstName: firstname, username, balance } = account;
    const transaction = useSelector(state => state.transaction);
    const transactionTypeId = transaction.transactionType.id;
    const transactionAmount = transaction.transactionAmount || '';
    const transactionDate = transaction.transactionTime;
    const dispatch = useDispatch();
    const [ postJobRegister ] = usePostJobRegisterMutation();
    const onRegisterJobClicked = async () => await postJobRegister({ account, transactionTypeId, transactionAmount, transactionDate });
    const transactionDateJustDate = transactionDate.split('T')[0];
    const title = text.weeklyAllowanceFor + ' ' + firstname;
    const performedjobs = '/performedjobs?' + stringify({ accountId, username, parentTitle: title });
    const performedpayments = '/performedpayments?' + stringify({ accountId, username, parentTitle: title });
    const statistics = '/statistics?' + stringify({ username });

    return (
        <div>
            <Notifier />
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
                            {balance}
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
                                onClick={onRegisterJobClicked}>
                                {text.registerJob}
                            </button>
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
