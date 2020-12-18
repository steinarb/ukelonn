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
import Locale from './Locale';
import PaymenttypesBox from './PaymenttypesBox';
import Amount from './Amount';

function AdminPaymenttypesModify(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, transactiontype, paymenttypes, onPaymenttypeFieldChange, onNameFieldChange, onAmountFieldChange, onSaveUpdatedPaymentType, onLogout } = props;

    const reduceHeaderRowPadding = { padding: '0 0 0 0' };

    return (
        <div>
            <Link to="/ukelonn/admin/paymenttypes">
                &lt;-
                &nbsp;
                {text.administratePaymenttypes}
            </Link>
            <header>
                <div>
                    <h1>{text.modifyPaymenttypes}</h1>
                </div>
                <div>
                    <Locale />
                </div>
            </header>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="paymenttype">{text.choosePaymentType}</label>
                        <div>
                            <PaymenttypesBox id="paymenttype" value={transactiontype.id}  paymenttypes={paymenttypes} onPaymenttypeFieldChange={onPaymenttypeFieldChange} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.modifyPaymentTypeName}</label>
                        <div>
                            <input id="name" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.modifyPaymentTypeAmount}</label>
                        <div>
                            <Amount id="amount" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => onSaveUpdatedPaymentType(transactiontype)}>{text.saveChangesToPaymentType}</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
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
