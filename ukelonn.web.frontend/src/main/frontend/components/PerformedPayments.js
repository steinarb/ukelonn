import React from 'react';
import { useSelector } from 'react-redux';
import { Link, useSearchParams } from 'react-router-dom';
import Locale from './Locale';
import Logout from './Logout';

export default function PerformedPayments() {
    const text = useSelector(state => state.displayTexts);
    const accountFirstname = useSelector(state => state.accountFirstname);
    const payments = useSelector(state => state.payments);
    const [ queryParams ] = useSearchParams();
    const parentTitle = queryParams.get('parentTitle');

    return (
        <div>
            <nav>
                <Link to="/ukelonn/">
                    &lt;-
                    &nbsp;
                    {parentTitle}
                </Link>
                <h1>{text.performedPaymentsFor} {accountFirstname}</h1>
                <Locale />
            </nav>
            <div>
                <table className="table table-bordered">
                    <thead>
                        <tr>
                            <th>{text.date}</th>
                            <th>{text.paymentType}</th>
                            <th>{text.amount}</th>
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
            <br/>
            <br/>
            <Logout/>
            <br/>
            <a href="../..">{text.returnToTop}</a>
        </div>
    );
}
