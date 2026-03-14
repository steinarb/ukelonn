import React from 'react';
import { setName, setAmount } from '../reducers/transactionTypeSlice';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostPaymenttypeCreateMutation,
} from '../api';
import { Link } from 'react-router';
import Locale from './Locale';
import Logout from './Logout';
import { numberAsString } from './utils';

export default function AdminPaymenttypesCreate() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const transactionType = useSelector(state => state.transactionType);
    const transactionTypeName =  transactionType.transactionTypeName;
    const transactionAmount = numberAsString(transactionType.transactionAmount);
    const dispatch = useDispatch();
    const [ postPaymenttypeCreate ] = usePostPaymenttypeCreateMutation();
    const onCreatePaymentTypeClicked = async () => await postPaymenttypeCreate({ transactionTypeName, transactionAmount });

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
                                onChange={e => dispatch(setName(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.paymentTypeAmount}</label>
                        <div>
                            <input
                                id="amount"
                                type="text"
                                value={transactionAmount}
                                onChange={e => dispatch(setAmount(parseInt(e.target.value)))} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button
                                onClick={onCreatePaymentTypeClicked}>
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
