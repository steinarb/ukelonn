import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { parse } from 'qs';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';

function PerformedPayments(props) {
    const {
        text,
        accountFirstname,
        payments,
        onLogout
    } = props;
    let queryParams = parse(props.location.search, { ignoreQueryPrefix: true });
    const { parentTitle } = queryParams;

    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

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
            <button className="btn btn-default" onClick={() => onLogout()}>{text.logout}</button>
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

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(PerformedPayments);
