import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostPaymenttypeModifyMutation,
} from '../api';
import { Link } from 'react-router';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_PAYMENT_AMOUNT,
} from '../actiontypes';
import Locale from './Locale';
import PaymenttypesBox from './PaymenttypesBox';
import Logout from './Logout';

export default function AdminPaymenttypesModify() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const id = useSelector(state => state.transactionTypeId);
    const transactionTypeName = useSelector(state => state.transactionTypeName);
    const transactionAmount = useSelector(state => state.transactionAmount);
    const dispatch = useDispatch();
    const [ postPaymenttypeModify ] = usePostPaymenttypeModifyMutation();
    const onSaveChangesToPaymenttypeClicked = async () => await postPaymenttypeModify({ id, transactionTypeName, transactionAmount });

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/admin/paymenttypes">
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
                    <div className="form-group row mb-2">
                        <label htmlFor="paymenttype" className="col-form-label col-5">{text.choosePaymentType}</label>
                        <div className="col-7">
                            <PaymenttypesBox id="paymenttype" className="form-control" />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="amount" className="col-form-label col-5">{text.modifyPaymentTypeName}</label>
                        <div className="col-7">
                            <input
                                id="name"
                                className="form-control"
                                type="text"
                                value={transactionTypeName}
                                onChange={e => dispatch(MODIFY_TRANSACTION_TYPE_NAME(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="amount" className="col-form-label col-5">{text.modifyPaymentTypeAmount}</label>
                        <div className="col-7">
                            <input
                                id="amount"
                                className="form-control"
                                type="text"
                                value={transactionAmount}
                                onChange={e => dispatch(MODIFY_PAYMENT_AMOUNT(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button
                                className="btn btn-primary"
                                onClick={onSaveChangesToPaymenttypeClicked}>
                                {text.saveChangesToPaymentType}
                            </button>
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
