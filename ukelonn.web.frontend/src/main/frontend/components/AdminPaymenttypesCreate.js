import React from 'react';
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
                <div>
                    <div>
                        <label htmlFor="amount">{text.paymentTypeName}</label>
                        <div>
                            <input id="name" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.paymentTypeAmount}</label>
                        <div>
                            <Amount id="amount" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => onSaveUpdatedPaymentType(transactiontype)}>{text.createNewPaymentType}</button>
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
