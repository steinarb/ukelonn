import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostPaymentRegisterMutation,
    usePostNotificationToMutation,
} from '../api';
import { setAmount } from '../reducers/transactionSlice';
import { Link } from 'react-router';
import { stringify } from 'qs';
import Locale from './Locale';
import BonusBanner from './BonusBanner';
import Accounts from './Accounts';
import Paymenttypes from './Paymenttypes';
import EarningsMessage from './EarningsMessage';
import Logout from './Logout';

export default function Admin() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const account = useSelector(state => state.account);
    const username = account.username;
    const transaction = useSelector(state => state.transaction);
    const transactionTypeId = transaction.transactionType.id;
    const transactionTypeName = transaction.transactionType.transactionTypeName;
    const transactionAmount = transaction.transactionAmount;
    const [ postPaymentRegister ] = usePostPaymentRegisterMutation();
    const [ postNotificationTo ] = usePostNotificationToMutation();
    const onRegisterPaymentClicked = async () => {
        await postPaymentRegister({ account, transactionTypeId, transactionAmount });
        const notification = { title: 'Ukelønn', message: transactionAmount + ' kroner ' + transactionTypeName };
        await postNotificationTo({ username, notification });
    };
    const dispatch = useDispatch();
    const parentTitle = 'Tilbake til ukelonn admin';
    const noUser = !username;
    const performedjobs = noUser ? '#' : '/performedjobs?' + stringify({ parentTitle, accountId: account.accountId, username });
    const performedpayments = noUser ? '#' : '/performedpayments?' + stringify({ parentTitle, accountId: account.accountId, username });
    const statistics = noUser ? '#' : '/statistics?' + stringify({ username });

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <a className="btn btn-primary" href="../.."><span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>&nbsp;{text.returnToTop}</a>
                <h1>{text.registerPayment}</h1>
                <Locale />
            </nav>
            <div>
                <BonusBanner/>
            </div>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row mb-2">
                        <label htmlFor="account-selector" className="col-form-label col-5">{text.chooseWhoToPayTo}:</label>
                        <div className="col-7">
                            <Accounts id="account-selector" className="form-control" />
                        </div>
                    </div>
                    <div className="row">
                        <EarningsMessage />
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="account-balance" className="col-form-label col-5">{text.owedAmount}:</label>
                        <div className="col-7">
                            <input id="account-balance" className="form-control" type="text" value={account.balance} readOnly={true} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="paymenttype-selector" className="col-form-label col-5">{text.paymentType}:</label>
                        <div className="col-7">
                            <Paymenttypes id="paymenttype-selector" className="form-control" />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="amount" className="col-form-label col-5">{text.amount}:</label>
                        <div className="col-7">
                            <input
                                id="amount"
                                className="form-control"
                                type="text"
                                value={transactionAmount}
                                onChange={e => dispatch(setAmount(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <div className="col-5" />
                        <div className="col-7">
                            <button
                                className="btn btn-primary"
                                disabled={noUser}
                                onClick={onRegisterPaymentClicked}>
                                {text.registerPayment}
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
                <Link className="btn btn-block btn-primary right-align-cell mb-2" to="/admin/jobtypes">
                    {text.administrateJobsAndJobTypes}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell mb-2" to="/admin/paymenttypes">
                    {text.administratePaymenttypes}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell mb-2" to="/admin/users">
                    {text.administrateUsers}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell mb-2" to="/admin/bonuses">
                    {text.administrateBonuses}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
            </div>
            <br/>
            <Logout />
        </div>
    );
}
