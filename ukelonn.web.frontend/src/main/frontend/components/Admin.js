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
            <nav>
                <a href="../..">&lt;-&nbsp;{text.returnToTop}</a>
                <h1>{text.registerPayment}</h1>
                <Locale />
            </nav>
            <div>
                <BonusBanner/>
            </div>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="account-selector">{text.chooseWhoToPayTo}:</label>
                        <div>
                            <Accounts id="account-selector" />
                        </div>
                    </div>
                    <EarningsMessage />
                    <br/>
                    <div>
                        <label htmlFor="account-balance">{text.owedAmount}:</label>
                        <div>
                            <input id="account-balance" type="text" value={account.balance} readOnly={true} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="paymenttype-selector">{text.paymentType}:</label>
                        <div>
                            <Paymenttypes id="paymenttype-selector" />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.amount}:</label>
                        <div>
                            <input
                                id="amount"
                                type="text"
                                value={transactionAmount}
                                onChange={e => dispatch(setAmount(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <div>
                            <button
                                disabled={noUser}
                                onClick={onRegisterPaymentClicked}>
                                {text.registerPayment}
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
                <Link to="/admin/jobtypes">
                    {text.administrateJobsAndJobTypes}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/admin/paymenttypes">
                    {text.administratePaymenttypes}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/admin/users">
                    {text.administrateUsers}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/admin/bonuses">
                    {text.administrateBonuses}
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
