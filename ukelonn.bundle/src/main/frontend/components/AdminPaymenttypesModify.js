import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import Paymenttypes from './Paymenttypes';
import Amount from './Amount';

class AdminPaymenttypesModify extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onPaymenttypeList();
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let { haveReceivedResponseFromLogin, loginResponse, paymenttypes, paymenttypesMap, transactiontype, onPaymenttypeFieldChange, onNameFieldChange, onAmountFieldChange, onSaveUpdatedPaymentType, onLogout } = this.state;

        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        return (
            <div>
                <h1>Endre betalingstyper</h1>
                <br/>
                <Link to="/ukelonn/admin/paymenttypes">Administer betalingstyper</Link>
                <br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="paymenttype">Velg betalingstype</label>
                    <Paymenttypes id="paymenttype" paymenttypes={paymenttypes} paymenttypesMap={paymenttypesMap} value={transactiontype.transactionTypeName} onPaymenttypeFieldChange={onPaymenttypeFieldChange} />
                    <br/>
                    <label htmlFor="amount">Endre navn på betalingstype</label>
                    <input id="name" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value, transactiontype)} />
                    <br/>
                    <label htmlFor="amount">Endre beløp for betalingstype</label>
                    <Amount id="amount" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                    <br/>
                    <button onClick={() => onSaveUpdatedPaymentType(transactiontype)}>Lagre endringer i betalingstype</button>
                </form>
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
            </div>
        );
    };
};

const emptyPaymenttype = {
    id: -1,
    transactionName: '',
    transactionAmount: 0.0
};


const mapStateToProps = state => {
    if (!state.paymenttypes.find((payment) => payment.id === -1)) {
        state.paymenttypes.unshift(emptyPaymenttype);
    }

    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        paymenttypes: state.paymenttypes,
        paymenttypesMap: new Map(state.paymenttypes.map(i => [i.transactionTypeName, i])),
        transactiontype: state.transactiontype,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onPaymenttypeList: () => dispatch({ type: 'PAYMENTTYPELIST_REQUEST' }),
        onPaymenttypeFieldChange: (selectedValue, paymenttypesMap, account, performedpayment) => {
            let paymenttype = paymenttypesMap.get(selectedValue);
            let changedField = {
                transactiontype: {...paymenttype},
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onNameFieldChange: (formValue, transactiontype) => {
            let changedField = {
                transactiontype: { ...transactiontype, transactionTypeName: formValue }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onAmountFieldChange: (formValue, transactiontype) => {
            let changedField = {
                transactiontype: { ...transactiontype, transactionAmount: formValue }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onSaveUpdatedPaymentType: (transactiontype) => dispatch({ type: 'MODIFY_PAYMENTTYPE_REQUEST', transactiontype }),
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
    };
};

AdminPaymenttypesModify = connect(mapStateToProps, mapDispatchToProps)(AdminPaymenttypesModify);

export default AdminPaymenttypesModify;
