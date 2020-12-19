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
import Locale from './Locale';
import Paymenttypes from './Paymenttypes';
import Amount from './Amount';

function AdminPaymenttypesCreate(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, transactiontype, onNameFieldChange, onAmountFieldChange, onSaveUpdatedPaymentType, onLogout } = props;

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/paymenttypes">
                    &lt;-
                    &nbsp;
                    {text.administratePaymenttypes}
                </Link>
                <h1>{text.createPaymenttype}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-5">{text.paymentTypeName}</label>
                        <div className="col-7">
                            <input id="name" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value, transactiontype)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-5">{text.paymentTypeAmount}</label>
                        <div className="col-7">
                            <Amount id="amount" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => onSaveUpdatedPaymentType(transactiontype)}>{text.createNewPaymentType}</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
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
        text: state.displayTexts,
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
