import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    UPDATE,
    CREATE_PAYMENTTYPE_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Paymenttypes from './Paymenttypes';
import Amount from './Amount';

class AdminPaymenttypesCreate extends Component {
    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let { paymenttypes, paymenttypesMap, transactiontype, onPaymenttypeFieldChange, onNameFieldChange, onAmountFieldChange, onSaveUpdatedPaymentType, onLogout } = this.props;

        return (
            <div>
                <h1>Lag ny utbetalingstype</h1>
                <br/>
                <Link to="/ukelonn/admin/paymenttypes">Administer utbetalingstyper</Link>
                <br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="amount">Navn på utbetalingstype</label>
                    <input id="name" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value, transactiontype)} />
                    <br/>
                    <label htmlFor="amount">Beløp for utbetalingstype</label>
                    <Amount id="amount" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                    <br/>
                    <button onClick={() => onSaveUpdatedPaymentType(transactiontype)}>Lag ny utbetalingstype</button>
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


function mapStateToProps(state) {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        paymenttypes: state.paymenttypes,
        transactiontype: state.transactiontype,
    };
}

const mapDispatchToProps = dispatch => {
    return {
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
        onSaveUpdatedPaymentType: (transactiontype) => dispatch(CREATE_PAYMENTTYPE_REQUEST(transactiontype)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
};

AdminPaymenttypesCreate = connect(mapStateToProps, mapDispatchToProps)(AdminPaymenttypesCreate);

export default AdminPaymenttypesCreate;
