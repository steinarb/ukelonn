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
        const parentTitle = queryParams.parentTitle ? queryParams.parentTitle : "Register betaling";
        this.props.onParentTitle(parentTitle);

        if (account.firstName === "Ukjent" && queryParams.username) {
            this.props.onAccount(queryParams.username);
        }
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let { haveReceivedResponseFromLogin, loginResponse, parentTitle, account, payments, onLogout } = this.state;
        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        const reduceHeaderRowPadding = { padding: '0 0 0 0' };

        return (
            <div className="mdl-layout mdl-layout--fixed-header">
                <header className="mdl-layout__header">
                    <div className="mdl-layout__header-row" style={reduceHeaderRowPadding}>
                        <Link to="/ukelonn/" className="mdl-navigation__link">
                            <i className="material-icons" >chevron_left</i>
                            &nbsp;
                            {parentTitle}</Link>
                        <span className="mdl-layout-title">Siste utbetalinger</span>
                    </div>
                </header>
                <main className="mdl-layout__content">
                    <table className="mdl-data-table mdl-js-data-table transaction-table">
                        <thead>
                            <tr>
                                <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col1">Dato</td>
                                <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col-hide-overflow transaction-table-col2">Utbetalinger</td>
                                <td className="transaction-table-col transaction-table-col3b">Beløp</td>
                            </tr>
                        </thead>
                        <tbody>
                            {payments.map((payment) =>
                                <tr key={payment.id}>
                                     <td className="mdl-data-table__cell--non-numeric transaction-table-col">{moment(payment.transactionTime).format("YYYY-MM-DD")}</td>
                                     <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col-hide-overflow">{payment.name}</td>
                                     <td className="transaction-table-col">{payment.transactionAmount}</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </main>
                <br/>
                <br/>
                <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onLogout()}>Logout</button>
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
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
        onAccount: (username) => dispatch({ type: 'ACCOUNT_REQUEST', username }),
        onPayments: (accountId) => dispatch({ type: 'RECENTPAYMENTS_REQUEST', accountId: accountId }),
        onParentTitle: (parentTitle) => dispatch({ type: 'UPDATE', data: { parentTitle } }),
    };
};

PerformedPayments = connect(mapStateToProps, mapDispatchToProps)(PerformedPayments);

export default PerformedPayments;
