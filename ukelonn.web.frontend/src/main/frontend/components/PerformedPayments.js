import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { parse } from 'qs';
import moment from 'moment';
import {
    LOGOUT_REQUEST,
    ACCOUNT_REQUEST,
    RECENTPAYMENTS_REQUEST,
    UPDATE,
} from '../actiontypes';

class PerformedPayments extends Component {
    componentDidMount() {
        let { account } = this.props;
        let queryParams = parse(this.props.location.search, { ignoreQueryPrefix: true });
        const accountId = account.firstName === 'Ukjent' ? queryParams.accountId : account.accountId;
        this.props.onPayments(accountId);
        const parentTitle = queryParams.parentTitle ? queryParams.parentTitle : 'Register betaling';
        this.props.onParentTitle(parentTitle);

        if (account.firstName === 'Ukjent' && queryParams.username) {
            this.props.onAccount(queryParams.username);
        }
    }

    render() {
        let { haveReceivedResponseFromLogin, loginResponse, parentTitle, account, payments, onLogout } = this.props;
        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

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
};

const mapStateToProps = state => {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        parentTitle: state.parentTitle,
        account: state.account,
        payments: state.payments,
    };
};
const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
        onAccount: (username) => dispatch(ACCOUNT_REQUEST(username)),
        onPayments: (accountId) => dispatch(RECENTPAYMENTS_REQUEST(accountId)),
        onParentTitle: (parentTitle) => dispatch(UPDATE({ parentTitle })),
    };
};

PerformedPayments = connect(mapStateToProps, mapDispatchToProps)(PerformedPayments);

export default PerformedPayments;
