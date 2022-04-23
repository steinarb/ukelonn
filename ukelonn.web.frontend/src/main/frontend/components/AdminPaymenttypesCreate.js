import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_JOB_AMOUNT,
    CREATE_PAYMENTTYPE_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import Logout from './Logout';

function AdminPaymenttypesCreate(props) {
    const {
        text,
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
                <h1>{text.createPaymenttype}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="amount">{text.paymentTypeName}</label>
                        <div>
                            <input id="name" type="text" value={transactionTypeName} onChange={e => dispatch(MODIFY_TRANSACTION_TYPE_NAME(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.paymentTypeAmount}</label>
                        <div>
                            <input id="amount" type="text" value={transactionAmount} onChange={e => dispatch(MODIFY_JOB_AMOUNT(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => dispatch(CREATE_PAYMENTTYPE_REQUEST({ transactionTypeName, transactionAmount }))}>{text.createNewPaymentType}</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <Logout />
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        transactionTypeName: state.transactionTypeName,
        transactionAmount: state.transactionAmount,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        transactiontype: state.transactiontype,
    };
}

export default connect(mapStateToProps)(AdminPaymenttypesCreate);
