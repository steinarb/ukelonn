import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_JOB_AMOUNT,
    CREATE_NEW_JOB_TYPE_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import Logout from './Logout';
import { numberAsString } from './utils';

export default function AdminJobtypesCreate() {
    const text = useSelector(state => state.displayTexts);
    const transactionTypeName = useSelector(state => state.transactionTypeName);
    const transactionAmount = useSelector(state => numberAsString(state.transactionAmount));
    const dispatch = useDispatch();

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/admin/jobtypes">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.createNewJobType}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">{text.nameOfJobType}</label>
                        <div className="col-7">
                            <input
                                id="name"
                                className="form-control"
                                type="text"
                                value={transactionTypeName}
                                onChange={e => dispatch(MODIFY_TRANSACTION_TYPE_NAME(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">{text.amountForJobType}</label>
                        <div className="col-7">
                            <input
                                id="amount"
                                className="form-control"
                                type="text"
                                value={transactionAmount}
                                onChange={e => dispatch(MODIFY_JOB_AMOUNT(parseInt(e.target.value)))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button
                                className="btn btn-primary"
                                onClick={() => dispatch(CREATE_NEW_JOB_TYPE_BUTTON_CLICKED())}>
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
