import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { parse } from 'qs';
import { userIsNotLoggedIn } from '../common/login';
import Locale from './Locale';
import Logout from './Logout';

function PerformedPayments(props) {
    const {
        text,
        accountFirstname,
        payments,
    } = props;
    let queryParams = parse(props.location.search, { ignoreQueryPrefix: true });
    const { parentTitle } = queryParams;

    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

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

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        parentTitle: state.parentTitle,
        accountFirstname: state.accountFirstname,
        payments: state.payments,
    };
}

export default connect(mapStateToProps)(PerformedPayments);
