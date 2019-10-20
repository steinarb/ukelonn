import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    UPDATE,
    PAYMENTTYPELIST_REQUEST,
    MODIFY_PAYMENTTYPE_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import PaymenttypesBox from './PaymenttypesBox';
import Amount from './Amount';

class AdminPaymenttypesModify extends Component {
    componentDidMount() {
        this.props.onPaymenttypeList();
    }

    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let { paymenttypes, paymenttypesMap, transactiontype, onPaymenttypeFieldChange, onNameFieldChange, onAmountFieldChange, onSaveUpdatedPaymentType, onLogout } = this.props;

        const reduceHeaderRowPadding = { padding: '0 0 0 0' };

        return (
            <div>
                <h1>Endre utbetalingstyper</h1>
                <br/>
                <Link to="/ukelonn/admin/paymenttypes">Administer utbetalingstyper</Link>
                <br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="paymenttype">Velg betalingstype</label>
                    <PaymenttypesBox id="paymenttype" paymenttypes={paymenttypes} paymenttypesMap={paymenttypesMap} value={transactiontype.transactionTypeName} onPaymenttypeFieldChange={onPaymenttypeFieldChange} />
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
                <br/>
                <a href="../../../..">Tilbake til topp</a>
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
        onPaymenttypeList: () => dispatch(PAYMENTTYPELIST_REQUEST()),
        onPaymenttypeFieldChange: (selectedValue, paymenttypesMap, account, performedpayment) => {
            let paymenttype = paymenttypesMap.get(selectedValue);
            let changedField = {
                transactiontype: {...paymenttype},
            };
            dispatch(UPDATE(changedField));
        },
        onNameFieldChange: (formValue, transactiontype) => {
            let changedField = {
                transactiontype: { ...transactiontype, transactionTypeName: formValue }
            };
            dispatch(UPDATE(changedField));
        },
        onAmountFieldChange: (formValue, transactiontype) => {
            let changedField = {
                transactiontype: { ...transactiontype, transactionAmount: formValue }
            };
            dispatch(UPDATE(changedField));
        },
        onSaveUpdatedPaymentType: (transactiontype) => dispatch(MODIFY_PAYMENTTYPE_REQUEST(transactiontype)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
};

AdminPaymenttypesModify = connect(mapStateToProps, mapDispatchToProps)(AdminPaymenttypesModify);

export default AdminPaymenttypesModify;
