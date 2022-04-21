import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_PAYMENT_AMOUNT,
    MODIFY_PAYMENTTYPE_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import PaymenttypesBox from './PaymenttypesBox';
import Logout from './Logout';

function AdminPaymenttypesModify(props) {
    const {
        text,
        transactionTypeId,
        transactionTypeName,
        transactionAmount,
        onNameFieldChange,
        onAmountFieldChange,
        onSaveUpdatedPaymentType,
    } = props;

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
                            <PaymenttypesBox id="paymenttype" className="form-control" />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">{text.modifyPaymentTypeName}</label>
                        <div className="col-7">
                            <input id="name" className="form-control" type="text" value={transactionTypeName} onChange={onNameFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">{text.modifyPaymentTypeAmount}</label>
                        <div className="col-7">
                            <input id="amount" className="form-control" type="text" value={transactionAmount} onChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => onSaveUpdatedPaymentType({ id: transactionTypeId, transactionTypeName, transactionAmount })}>{text.saveChangesToPaymentType}</button>
                        </div>
                    </div>
                </div>
                <br/>
            </form>
            <br/>
            <Logout/>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        transactionTypeId: state.transactionTypeId,
        transactionTypeName: state.transactionTypeName,
        transactionAmount: state.transactionAmount,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onNameFieldChange: e => dispatch(MODIFY_TRANSACTION_TYPE_NAME(e.target.value)),
        onAmountFieldChange: e => dispatch(MODIFY_PAYMENT_AMOUNT(e.target.value)),
        onSaveUpdatedPaymentType: transactiontype => dispatch(MODIFY_PAYMENTTYPE_REQUEST(transactiontype)),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminPaymenttypesModify);
