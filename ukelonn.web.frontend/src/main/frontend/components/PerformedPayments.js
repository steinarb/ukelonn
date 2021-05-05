import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { parse } from 'qs';
import moment from 'moment';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';

function PerformedPayments(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, account, payments, onLogout } = props;
    let queryParams = parse(props.location.search, { ignoreQueryPrefix: true });
    const { parentTitle } = queryParams;

    return (
        <div>
            <nav>
                <Link to="/ukelonn/">
                    &lt;-
                    &nbsp;
                    {parentTitle}
                </Link>
                <h1>{text.performedPaymentsFor} {account.firstName}</h1>
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
                                          <td>{moment(payment.transactionTime).format('YYYY-MM-DD')}</td>
                                          <td>{payment.name}</td>
                                          <td>{payment.transactionAmount}</td>
                                      </tr>
                                     )}
                    </tbody>
                </table>
            </div>
            <br/>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
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
        account: state.account,
        payments: state.payments,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(PerformedPayments);
