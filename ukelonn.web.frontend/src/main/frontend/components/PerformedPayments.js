import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';

class PerformedPayments extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onPayments(this.props.account);
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let { loginResponse, account, payments, onLogout } = this.state;
        if (loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        return (
            <div>
                <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    Register betaling
                </Link>
                <header>
                    <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                        <h1>Utbetalinger til {account.firstName}</h1>
                    </div>
                </header>
                <div className="table-responsive table-sm table-striped">
                    <table className="table">
                        <thead>
                            <tr>
                                <th className="transaction-table-col transaction-table-col1">Dato</th>
                                <th className="transaction-table-col transaction-table-col-hide-overflow transaction-table-col2">Utbetalinger</th>
                                <th className="transaction-table-col transaction-table-col3b">Beløp</th>
                            </tr>
                        </thead>
                        <tbody>
                            {payments.map((payment) =>
                                 <tr key={payment.id}>
                                     <td className="transaction-table-col">{payment.transactionTime}</td>
                                     <td className="transaction-table-col">{payment.name}</td>
                                     <td className="transaction-table-col">{payment.transactionAmount}</td>
                                 </tr>
                            )}
                        </tbody>
                    </table>
                </div>
                <button className="btn btn-default" onClick={() => onLogout()}>Logout</button>
            </div>
        );
    }
};

const mapStateToProps = state => {
    return {
        loginResponse: state.loginResponse,
        account: state.account,
        payments: state.payments,
    };
};
const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
        onPayments: (account) => dispatch({ type: 'RECENTPAYMENTS_REQUEST', account: account }),
    };
};

PerformedPayments = connect(mapStateToProps, mapDispatchToProps)(PerformedPayments);

export default PerformedPayments;
