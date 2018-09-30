import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { parse } from 'qs';
import moment from 'moment';

class PerformedPayments extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        let { account } = this.props;
        let queryParams = parse(this.props.location.search, { ignoreQueryPrefix: true });
        const accountId = account.firstName === "Ukjent" ? queryParams.accountId : account.accountId;
        this.props.onPayments(accountId);

        if (account.firstName === "Ukjent" && queryParams.username) {
            this.props.onAccount(queryParams.username);
        }
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let { haveReceivedResponseFromLogin, loginResponse, account, payments, onLogout } = this.state;
        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        return (
            <div>
                <Link to="/ukelonn/">Register betaling</Link>
                <br/>
                <h1>Utførte jobber for {account.firstName}</h1>
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
                                <td>{moment(payment.transactionTime).format("YYYY-MM-DD")}</td>
                                <td>{payment.name}</td>
                                <td>{payment.transactionAmount}</td>
                            </tr>
                        )}
                    </tbody>
                </table>
                <br/>
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
            </div>
        );
    }
};

const mapStateToProps = state => {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        account: state.account,
        payments: state.payments,
    };
};
const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
        onAccount: (username) => dispatch({ type: 'ACCOUNT_REQUEST', username }),
        onPayments: (accountId) => dispatch({ type: 'RECENTPAYMENTS_REQUEST', accountId: accountId }),
    };
};

PerformedPayments = connect(mapStateToProps, mapDispatchToProps)(PerformedPayments);

export default PerformedPayments;
