import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    UPDATE_TRANSACTIONTYPE,
    MODIFY_PAYMENTTYPE_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import PaymenttypesBox from './PaymenttypesBox';
import Amount from './Amount';

function AdminPaymenttypesModify(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { transactiontype, paymenttypes, onPaymenttypeFieldChange, onNameFieldChange, onAmountFieldChange, onSaveUpdatedPaymentType, onLogout } = props;

    const reduceHeaderRowPadding = { padding: '0 0 0 0' };

    return (
        <div>
            <h1>Endre utbetalingstyper</h1>
            <br/>
            <Link to="/ukelonn/admin/paymenttypes">Administer utbetalingstyper</Link>
            <br/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <label htmlFor="paymenttype">Velg betalingstype</label>
                <PaymenttypesBox id="paymenttype" value={transactiontype.id}  paymenttypes={paymenttypes} onPaymenttypeFieldChange={onPaymenttypeFieldChange} />
                <br/>
                <label htmlFor="amount">Endre navn på betalingstype</label>
                <input id="name" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value)} />
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
}

function mapStateToProps(state) {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        paymenttypes: state.paymenttypes,
        transactiontype: state.transactiontype,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onPaymenttypeFieldChange: (selectedValue, paymenttypes, account, performedpayment) => {
            const selectedValueInt = parseInt(selectedValue, 10);
            let paymenttype = paymenttypes.find(pt => selectedValueInt == pt.id);
            dispatch(UPDATE_TRANSACTIONTYPE({ ...paymenttype }));
        },
        onNameFieldChange: (transactionTypeName) => dispatch(UPDATE_TRANSACTIONTYPE({ transactionTypeName })),
        onAmountFieldChange: (transactionAmount) => dispatch(UPDATE_TRANSACTIONTYPE({ transactionAmount })),
        onSaveUpdatedPaymentType: (transactiontype) => dispatch(MODIFY_PAYMENTTYPE_REQUEST(transactiontype)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminPaymenttypesModify);
