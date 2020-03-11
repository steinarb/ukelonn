import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { parse } from 'qs';
import moment from 'moment';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';

function PerformedPayments(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { account, payments, onLogout } = props;
    let queryParams = parse(props.location.search, { ignoreQueryPrefix: true });
    const { parentTitle } = queryParams;

    return (
        <div>
            <Link to="/ukelonn/">{parentTitle}</Link>
            <br/>
            <h1>Utførte utbetalinger til {account.firstName}</h1>
            <table className="table table-bordered">
                <thead>
                    <tr>
                        <td>Dato</td>
                        <td>Utbetalinger</td>
                        <td>Beløp</td>
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
            <br/>
            <br/>
            <button onClick={() => onLogout()}>Logout</button>
            <br/>
            <a href="../..">Tilbake til topp</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
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
