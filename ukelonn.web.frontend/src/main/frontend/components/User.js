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
            <nav className="navbar navbar-light bg-light">
                <a className="btn btn-primary" href="../.."><span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>&nbsp;{text.returnToTop}</a>
                <h1 id="logo">{title}</h1>
                <Locale />
            </nav>
            <div className="container-fluid">
                <BonusBanner/>
                <div className="container">
                    <div className="row border rounded mb-3">
                        <table className="table">
                            <tr>
                                <td>{text.owedAmount}</td>
                                <td>{balance}</td>
                            </tr>
                        </table>
                    </div>
                    <div className="row">
                        <EarningsMessage />
                    </div>
                </div>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="container">
                        <div className="form-group row mb-2">
                            <label htmlFor="jobtype" className="col-form-label col-5">{text.chooseJob}</label>
                            <div className="col-7">
                                <Jobtypes id="jobtype" className="form-control" />
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
                        <div className="form-group row mb-2">
                            <div className="col-5"/>
                            <div className="col-7">
                                <button
                                    className="btn btn-primary"
                                    onClick={onRegisterJobClicked}>
                                    {text.registerJob}
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
                <div className="container">
                    <Link className="btn btn-block btn-primary right-align-cell mb-2" to={performedjobs}>
                        {text.performedJobs}
                        &nbsp;
                        <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                    </Link>
                    <Link className="btn btn-block btn-primary right-align-cell mb-2" to={performedpayments}>
                        {text.performedPayments}
                        &nbsp;
                        <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                    </Link>
                    <Link className="btn btn-block btn-primary right-align-cell mb-2" to={statistics}>
                        {text.statistics}
                        &nbsp;
                        <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                    </Link>
                </div>
                <br/>
                <br/>
                <Logout />
            </div>
        </div>
    );
}
