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
            <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/admin/paymenttypes">
                <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                &nbsp;
                Administer betalingstyper
            </Link>
            <header>
                <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                    <h1>Endre betalingstyper</h1>
                </div>
            </header>
            <br/>
            <br/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="paymenttype" className="col-form-label col-5">Velg betalingstype</label>
                        <div className="col-7">
                            <PaymenttypesBox id="paymenttype" className="form-control" value={transactiontype.id}  paymenttypes={paymenttypes} onPaymenttypeFieldChange={onPaymenttypeFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">Endre navn på betalingstype</label>
                        <div className="col-7">
                            <input id="name" className="form-control" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value, transactiontype)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">Endre beløp for betalingstype</label>
                        <div className="col-7">
                            <Amount id="amount" className="form-control" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => onSaveUpdatedPaymentType(transactiontype)}>Lagre endringer i betalingstype</button>
                        </div>
                    </div>
                </div>
                <br/>
            </form>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>Logout</button>
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
