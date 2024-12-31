import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostJobtypeModifyMutation,
} from '../api';
import { Link } from 'react-router';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_JOB_AMOUNT,
} from '../actiontypes';
import Locale from './Locale';
import JobtypesBox from './JobtypesBox';
import Logout from './Logout';
import { numberAsString } from './utils';

export default function AdminJobtypesModify() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const id = useSelector(state => numberAsString(state.transactionTypeId));
    const transactionAmount = useSelector(state => numberAsString(state.transactionAmount));
    const transactionTypeName = useSelector(state => state.transactionTypeName);
    const dispatch = useDispatch();
    const [ postJobtypeModify ] = usePostJobtypeModifyMutation();
    const onSaveChangesToJobtypeClicked = async () => await postJobtypeModify({ id, transactionTypeName, transactionAmount });

    return (
        <div>
            <nav>
                <Link to="/admin/jobtypes">
                    &lt;-
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.modifyJobTypes}</h1>
                <Locale />
            </nav>
            <div>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div>
                        <label htmlFor="jobtype">{text.chooseJobType}</label>
                        <div>
                            <JobtypesBox id="jobtype" />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.modifyNameOfJobType}</label>
                        <div>
                            <input
                                id="name"
                                type="text"
                                value={transactionTypeName}
                                onChange={e => dispatch(MODIFY_TRANSACTION_TYPE_NAME(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.modifyAmountOfJobType}</label>
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
                                {text.saveChangesToJobType}
                            </button>
                        </div>
                    </div>
            </form>
            </div>
            <br/>
            <Logout/>
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}
