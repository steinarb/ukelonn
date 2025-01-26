import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { setName, setAmount } from '../reducers/transactionTypeSlice';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostPaymenttypeModifyMutation,
} from '../api';
import { Link } from 'react-router';
import Locale from './Locale';
import PaymenttypesBox from './PaymenttypesBox';
import Logout from './Logout';

export default function AdminPaymenttypesModify() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const transactionType = useSelector(state => state.transactionType);
    const id = transactionType.id;
    const transactionTypeName = transactionType.transactionTypeName;
    const transactionAmount = transactionType.transactionAmount || '';
    const dispatch = useDispatch();
    const [ postPaymenttypeModify ] = usePostPaymenttypeModifyMutation();
    const onSaveChangesToPaymenttypeClicked = async () => await postPaymenttypeModify({ id, transactionTypeName, transactionAmount });

    return (
        <div>
            <nav>
                <Link to="/admin/paymenttypes">
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
                            <input
                                id="name"
                                type="text"
                                value={transactionTypeName}
                                onChange={e => dispatch(setName(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.modifyPaymentTypeAmount}</label>
                        <div>
                            <input
                                id="amount"
                                type="text"
                                value={transactionAmount}
                                onChange={e => dispatch(setAmount(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button
                                onClick={onSaveChangesToPaymenttypeClicked}>
                                {text.saveChangesToPaymentType}
                            </button>
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
