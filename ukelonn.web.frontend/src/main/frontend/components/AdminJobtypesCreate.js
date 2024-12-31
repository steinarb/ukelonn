import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostJobtypeCreateMutation,
} from '../api';
import { Link } from 'react-router';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_JOB_AMOUNT,
} from '../actiontypes';
import Locale from './Locale';
import Logout from './Logout';
import { numberAsString } from './utils';

export default function AdminJobtypesCreate() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const transactionTypeName = useSelector(state => state.transactionTypeName);
    const transactionAmount = useSelector(state => numberAsString(state.transactionAmount));
    const dispatch = useDispatch();
    const [ postJobtypeCreate ] = usePostJobtypeCreateMutation();
    const onSaveChangesToJobtypeClicked = async () => await postJobtypeCreate({ transactionTypeName, transactionAmount });

    return (
        <div>
            <nav>
                <Link to="/admin/jobtypes">
                    &lt;-
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.createNewJobType}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="amount">{text.nameOfJobType}</label>
                        <div>
                            <input
                                id="name"
                                type="text"
                                value={transactionTypeName}
                                onChange={e => dispatch(MODIFY_TRANSACTION_TYPE_NAME(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.amountForJobType}</label>
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
                                onClick={onSaveChangesToJobtypeClicked}>
                                {text.createNewJobType}
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
