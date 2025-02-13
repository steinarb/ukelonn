import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { setName, setAmount } from '../reducers/transactionTypeSlice';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostJobtypeCreateMutation,
} from '../api';
import { Link } from 'react-router';
import Locale from './Locale';
import Logout from './Logout';
import { numberAsString } from './utils';

export default function AdminJobtypesCreate() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const transactionType = useSelector(state => state.transactionType);
    const transactionTypeName = transactionType.transactionTypeName;
    const transactionAmount = numberAsString(transactionType.transactionAmount);
    const dispatch = useDispatch();
    const [ postJobtypeCreate ] = usePostJobtypeCreateMutation();
    const onSaveChangesToJobtypeClicked = async () => await postJobtypeCreate({ transactionTypeName, transactionAmount });

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/admin/jobtypes">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.createNewJobType}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row mb-2">
                        <label htmlFor="amount" className="col-form-label col-5">{text.nameOfJobType}</label>
                        <div className="col-7">
                            <input
                                id="name"
                                className="form-control"
                                type="text"
                                value={transactionTypeName}
                                onChange={e => dispatch(setName(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="amount" className="col-form-label col-5">{text.amountForJobType}</label>
                        <div className="col-7">
                            <input
                                id="amount"
                                className="form-control"
                                type="text"
                                value={transactionAmount}
                                onChange={e => dispatch(setAmount(parseInt(e.target.value)))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button
                                className="btn btn-primary"
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
