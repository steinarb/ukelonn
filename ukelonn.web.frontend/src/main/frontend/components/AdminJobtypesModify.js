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
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/admin/jobtypes">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.modifyJobTypes}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row mb-2">
                        <label htmlFor="jobtype" className="col-form-label col-5">{text.chooseJobType}</label>
                        <div className="col-7">
                            <JobtypesBox id="jobtype" className="form-control" />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="amount" className="col-form-label col-5">{text.modifyNameOfJobType}</label>
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
                        <label htmlFor="amount" className="col-form-label col-5">{text.modifyAmountOfJobType}</label>
                        <div className="col-7">
                            <input
                                id="amount"
                                className="form-control"
                                type="text"
                                value={transactionAmount}
                                onChange={e => dispatch(MODIFY_JOB_AMOUNT(parseInt(e.target.value)))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button
                                className="btn btn-primary"
                                onClick={onSaveChangesToJobtypeClicked}>
                                {text.saveChangesToJobType}
                            </button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <Logout/>
        </div>
    );
}
