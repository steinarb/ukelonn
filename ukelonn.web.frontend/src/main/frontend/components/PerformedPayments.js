import React from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { parse } from 'qs';
import Locale from './Locale';
import Logout from './Logout';

export default function PerformedPayments(props) {
    const text = useSelector(state => state.displayTexts);
    const accountFirstname = useSelector(state => state.accountFirstname);
    const payments = useSelector(state => state.payments);
    let queryParams = parse(props.location.search, { ignoreQueryPrefix: true });
    const { parentTitle } = queryParams;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/">
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
                        {payments.map((payment) =>
                            <tr key={payment.id}>
                                <td>{new Date(payment.transactionTime).toISOString().split('T')[0]}</td>
                                <td>{payment.name}</td>
                                <td>{payment.transactionAmount}</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
            <Logout/>
            <br/>
            <a href="../..">{text.returnToTop}</a>
        </div>
    );
}
