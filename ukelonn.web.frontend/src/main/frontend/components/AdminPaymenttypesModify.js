import React from 'react';
import { connect, useDispatch } from 'react-redux';
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
    } = props;
    const dispatch = useDispatch();

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/paymenttypes">
                    &lt;-
                    &nbsp;
                    {text.administratePaymenttypes}
                </Link>
                <h1>{text.modifyPaymenttypes}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="paymenttype">{text.choosePaymentType}</label>
                        <div>
                            <PaymenttypesBox id="paymenttype" />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.modifyPaymentTypeName}</label>
                        <div>
                            <input id="name" type="text" value={transactionTypeName} onChange={e => dispatch(MODIFY_TRANSACTION_TYPE_NAME(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.modifyPaymentTypeAmount}</label>
                        <div>
                            <input id="amount" type="text" value={transactionAmount} onChange={e => dispatch(MODIFY_PAYMENT_AMOUNT(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => dispatch(MODIFY_PAYMENTTYPE_REQUEST({ id: transactionTypeId, transactionTypeName, transactionAmount }))}>{text.saveChangesToPaymentType}</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <Logout/>
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
        transactionTypeId: state.transactionTypeId,
        transactionTypeName: state.transactionTypeName,
        transactionAmount: state.transactionAmount,
    };
}

export default connect(mapStateToProps)(AdminPaymenttypesModify);
