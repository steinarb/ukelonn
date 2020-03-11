import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    UPDATE_TRANSACTIONTYPE,
    CREATE_PAYMENTTYPE_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Paymenttypes from './Paymenttypes';
import Amount from './Amount';

function AdminPaymenttypesCreate(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let {  transactiontype, onNameFieldChange, onAmountFieldChange, onSaveUpdatedPaymentType, onLogout } = props;

    return (
        <div>
            <h1>Lag ny utbetalingstype</h1>
            <br/>
            <Link to="/ukelonn/admin/paymenttypes">Administer utbetalingstyper</Link>
            <br/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <label htmlFor="amount">Navn på utbetalingstype</label>
                <input id="name" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value)} />
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
}

const emptyPaymenttype = {
    id: -1,
    transactionName: '',
    transactionAmount: 0.0
};


function mapStateToProps(state) {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        transactiontype: state.transactiontype,
    };
}

const mapDispatchToProps = dispatch => {
    return {
        onNameFieldChange: (transactionTypeName) => dispatch(UPDATE_TRANSACTIONTYPE({ transactionTypeName })),
        onAmountFieldChange: (transactionAmount) => dispatch(UPDATE_TRANSACTIONTYPE({ transactionAmount })),
        onSaveUpdatedPaymentType: (transactiontype) => dispatch(CREATE_PAYMENTTYPE_REQUEST(transactiontype)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(AdminPaymenttypesCreate);
