import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_JOB_AMOUNT,
    CREATE_PAYMENT_TYPE_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import Logout from './Logout';
import { numberAsString } from './utils';

export default function AdminPaymenttypesCreate() {
    const text = useSelector(state => state.displayTexts);
    const transactionTypeName = useSelector(state => state.transactionTypeName);
    const transactionAmount = useSelector(state => numberAsString(state.transactionAmount));
    const dispatch = useDispatch();

    return (
        <div>
            <nav>
                <Link to="/admin/paymenttypes">
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
                            <input
                                id="name"
                                type="text"
                                value={transactionTypeName}
                                onChange={e => dispatch(MODIFY_TRANSACTION_TYPE_NAME(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.paymentTypeAmount}</label>
                        <div>
                            <input
                                id="amount"
                                type="text"
                                value={transactionAmount}
                                onChange={e => dispatch(MODIFY_JOB_AMOUNT(parseInt(e.target.value)))} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button
                                onClick={() => dispatch(CREATE_PAYMENT_TYPE_BUTTON_CLICKED())}>
                                {text.createNewPaymentType}
                            </button>
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
