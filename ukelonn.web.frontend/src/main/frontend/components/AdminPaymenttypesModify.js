import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    SELECT_PAYMENT_TYPE_FOR_EDIT,
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_PAYMENT_AMOUNT,
    MODIFY_PAYMENTTYPE_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import PaymenttypesBox from './PaymenttypesBox';

function AdminPaymenttypesModify(props) {
    const {
        text,
        paymenttypes,
        transactionTypeId,
        transactionTypeName,
        transactionAmount,
        onPaymenttypeFieldChange,
        onNameFieldChange,
        onAmountFieldChange,
        onSaveUpdatedPaymentType,
        onLogout,
    } = props;

    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/admin/paymenttypes">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administratePaymenttypes}
                </Link>
                <h1>{text.modifyPaymenttypes}</h1>
                <Locale />
            </nav>
            <br/>
            <br/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="paymenttype" className="col-form-label col-5">{text.choosePaymentType}</label>
                        <div className="col-7">
                            <PaymenttypesBox id="paymenttype" value={transactionTypeId}  paymenttypes={paymenttypes} onPaymenttypeFieldChange={onPaymenttypeFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">{text.modifyPaymentTypeName}</label>
                        <div className="col-7">
                            <input id="name" type="text" value={transactionTypeName} onChange={onNameFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">{text.modifyPaymentTypeAmount}</label>
                        <div className="col-7">
                            <input id="amount" type="text" value={transactionAmount} onChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button onClick={() => onSaveUpdatedPaymentType({ id: transactionTypeId, transactionTypeName, transactionAmount })}>{text.saveChangesToPaymentType}</button>
                        </div>
                    </div>
                </div>
                <br/>
            </form>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>{text.logout}</button>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        paymenttypes: state.paymenttypes,
        transactionTypeId: state.transactionTypeId,
        transactionTypeName: state.transactionTypeName,
        transactionAmount: state.transactionAmount,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onPaymenttypeFieldChange: selectedValue => dispatch(SELECT_PAYMENT_TYPE_FOR_EDIT(parseInt(selectedValue))),
        onNameFieldChange: e => dispatch(MODIFY_TRANSACTION_TYPE_NAME(e.target.value)),
        onAmountFieldChange: e => dispatch(MODIFY_PAYMENT_AMOUNT(e.target.value)),
        onSaveUpdatedPaymentType: transactiontype => dispatch(MODIFY_PAYMENTTYPE_REQUEST(transactiontype)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminPaymenttypesModify);
