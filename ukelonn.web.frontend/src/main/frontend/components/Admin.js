import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    ACCOUNTS_REQUEST,
    PAYMENTTYPES_REQUEST,
    UPDATE,
    REGISTERPAYMENT_REQUEST,
} from '../actiontypes';
import Accounts from './Accounts';
import Paymenttypes from './Paymenttypes';
import Amount from './Amount';

class Admin extends Component {
    componentDidMount() {
        this.props.onDeselectAccountInDropdown(this.props.firstTimeAfterLogin);
        this.props.onAccounts();
        this.props.onPaymenttypeList();
    }

    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let {
            account = {},
            payment,
            paymenttype,
            amount,
            accounts,
            accountsMap,
            paymenttypes,
            paymenttypesMap,
            onAccountsFieldChange,
            onPaymenttypeFieldChange,
            onAmountFieldChange,
            onRegisterPayment,
            onLogout } = this.props;

        const parentTitle = 'Tilbake til ukelonn admin';
        const accountId = account.accountId;
        const username = account.username;
        const performedjobs = "/ukelonn/performedjobs?" + stringify({ parentTitle, accountId, username });
        const performedpayments = "/ukelonn/performedpayments?" + stringify({ parentTitle, accountId, username });
        const statistics = '/ukelonn/statistics?' + stringify({ username });

        return (
            <div>
                <h1>Registrer betaling</h1>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="account-selector">Velg hvem det skal betales til:</label>
                    <Accounts  id="account-selector" accounts={accounts} accountsMap={accountsMap} account={account} paymenttype={paymenttype} onAccountsFieldChange={onAccountsFieldChange}/>
                    <br/>
                    <label htmlFor="account-balance">Til gode:</label><input id="account-balance" type="text" value={account.balance} readOnly="true" /><br/>
                    <label htmlFor="paymenttype-selector">Type av utbetaling:</label>
                    <Paymenttypes id="paymenttype-selector" value={paymenttype.transactionName} paymenttypes={paymenttypes} paymenttypesMap={paymenttypesMap} account={account} paymenttype={paymenttype} onPaymenttypeFieldChange={onPaymenttypeFieldChange} />
                    <br/>
                    <label htmlFor="amount">Beløp:</label>
                    <Amount id="amount" payment={payment} onAmountFieldChange={onAmountFieldChange} />
                    <br/>
                    <br/>
                    <button onClick={() => onRegisterPayment(payment, paymenttype)}>Registrer betaling</button>
                </form>
                <br/>
                <Link to={performedjobs}>Utforte jobber</Link><br/>
                <Link to={performedpayments}>Siste utbetalinger til bruker</Link><br/>
                <Link to={statistics}>Statistikker</Link><br/>
                <Link to="/ukelonn/admin/jobtypes">Administrer jobber og jobbtyper</Link><br/>
                <Link to="/ukelonn/admin/paymenttypes">Administrere utbetalingstyper</Link><br/>
                <Link to="/ukelonn/admin/users">Administrere brukere</Link><br/>
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
                <br/>
                <a href="../..">Tilbake til topp</a>
            </div>
        );
    };
};

const emptyAccount = {
    accountId: -1,
    fullName: '',
    balance: 0.0,
};

const mapStateToProps = state => {
    return {
        loginResponse: state.loginResponse,
        firstTimeAfterLogin: state.firstTimeAfterLogin,
        account: state.account,
        payment: state.payment,
        paymenttype: state.paymenttype,
        accounts: state.accounts,
        accountsMap: state.accountsMap,
        paymenttypes: state.paymenttypes,
        paymenttypesMap: new Map(state.paymenttypes.map(i => [i.transactionTypeName, i])),
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
        onDeselectAccountInDropdown: (firstTimeAfterLogin) => {
            if (firstTimeAfterLogin) {
                dispatch(UPDATE({
                    firstTimeAfterLogin: false,
                    account: emptyAccount,
                    payment: {
                        account: emptyAccount,
                        transactionAmount: 0.0,
                        transactionTypeId: -1
                    }
                }));
            }
        },
        onAccounts: () => dispatch(ACCOUNTS_REQUEST()),
        onPaymenttypeList: () => dispatch(PAYMENTTYPES_REQUEST()),
        onAccountsFieldChange: (selectedValue, accountsMap, paymenttype) => {
            let account = accountsMap.get(selectedValue);
            let amount = (paymenttype.transactionAmount > 0) ? paymenttype.transactionAmount : account.balance;
            let changedField = {
                account,
                payment: {
                    transactionTypeId: paymenttype.id,
                    transactionAmount: amount,
                    account: account,
                },
            };
            dispatch(UPDATE(changedField));
        },
        onPaymenttypeFieldChange: (selectedValue, paymenttypeMap, account) => {
            let paymenttype = paymenttypeMap.get(selectedValue);
            let amount = (paymenttype.transactionAmount > 0) ? paymenttype.transactionAmount : account.balance;
            let changedField = {
                paymenttype,
                payment: {
                    transactionTypeId: paymenttype.id,
                    transactionAmount: amount,
                    account: account,
                }
            };
            dispatch(UPDATE(changedField));
        },
        onAmountFieldChange: (formValue, payment) => {
            let changedField = {
                payment: { ...payment, transactionAmount: formValue }
            };
            dispatch(UPDATE(changedField));
        },
        onRegisterPayment: (payment, paymenttype) => dispatch(REGISTERPAYMENT_REQUEST({ payment, paymenttype })),
    };
};

Admin = connect(mapStateToProps, mapDispatchToProps)(Admin);

export default Admin;
