import React from 'react';
import { useSelector } from 'react-redux';
import { useSwipeable } from 'react-swipeable';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    useGetAccountQuery,
    useGetPaymentsInfiniteQuery,
} from '../api';
import { Link, useSearchParams } from 'react-router';
import Locale from './Locale';
import Logout from './Logout';

export default function PerformedPayments() {
    const [ queryParams ] = useSearchParams();
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const username = queryParams.get('username');
    const { data: account = {} } = useGetAccountQuery(username);
    const { firstName: accountFirstname } = account;
    const accountId = queryParams.get('accountId');
    const { data: payments, isSuccess: paymentsIsSuccess, fetchNextPage } = useGetPaymentsInfiniteQuery(accountId);
    const parentTitle = queryParams.get('parentTitle');
    const onNextPageClicked = async () => fetchNextPage();
    const swipeHandlers = useSwipeable({
        onSwipedUp: async () => fetchNextPage(),
    });

    return (
        <div {...swipeHandlers}>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {parentTitle}
                </Link>
                <h1>{text.performedPaymentsFor} {accountFirstname}</h1>
                <Locale />
            </nav>
            <div className="table-responsive table-sm table-striped">
                <table className="table">
                    <thead>
                        <tr>
                            <th className="transaction-table-col1">{text.date}</th>
                            <th className="transaction-table-col-hide-overflow transaction-table-col2">{text.paymentType}</th>
                            <th className="transaction-table-col3b">{text.amount}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {paymentsIsSuccess && payments.pages.map((page) => page.map((payment) =>
                            <tr key={payment.id}>
                                <td>{new Date(payment.transactionTime).toISOString().split('T')[0]}</td>
                                <td>{payment.name}</td>
                                <td>{payment.transactionAmount}</td>
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
