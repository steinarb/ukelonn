import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import Accounts from './Accounts';
import Paymenttypes from './Paymenttypes';
import Amount from './Amount';

class Admin extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onDeselectAccountInDropdown(this.state.firstTimeAfterLogin);
        this.props.onAccounts();
        this.props.onPaymenttypeList();
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let {
            loginResponse,
            account,
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
            onLogout } = this.state;

        if (loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        const performedjobs = "/ukelonn/performedjobs?" + stringify({ accountId: account.accountId, username: account.username });

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
                    <button onClick={() => onRegisterPayment(payment)}>Registrer betaling</button>
                </form>
                <br/>
                <Link to={performedjobs}>Utforte jobber</Link><br/>
                <Link to="/ukelonn/performedpayments">Siste utbetalinger til bruker</Link><br/>
                <Link to="/ukelonn/admin/jobtypes">Administrer jobber og jobbtyper</Link><br/>
                <Link to="/ukelonn/admin/paymenttypes">Administrere utbetalingstyper</Link><br/>
                <Link to="/ukelonn/admin/users">Administrere brukere</Link><br/>
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
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
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
        onDeselectAccountInDropdown: (firstTimeAfterLogin) => {
            if (firstTimeAfterLogin) {
                dispatch({ type: 'UPDATE',
                           data: {
                               firstTimeAfterLogin: false,
                               account: emptyAccount,
                               payment: {
                                   account: emptyAccount,
                                   transactionAmount: 0.0,
                                   transactionTypeId: -1
                               }
                           }
                         });
            }
        },
        onAccounts: () => dispatch({ type: 'ACCOUNTS_REQUEST' }),
        onPaymenttypeList: () => dispatch({ type: 'PAYMENTTYPES_REQUEST' }),
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
            dispatch({ type: 'UPDATE', data: changedField });
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
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onAmountFieldChange: (formValue, payment) => {
            let changedField = {
                payment: { ...payment, transactionAmount: formValue }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onRegisterPayment: (payment) => dispatch({ type: 'REGISTERPAYMENT_REQUEST', payment }),
    };
};

Admin = connect(mapStateToProps, mapDispatchToProps)(Admin);

export default Admin;
