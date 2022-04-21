import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_JOB_AMOUNT,
    MODIFY_JOBTYPE_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import JobtypesBox from './JobtypesBox';
import Logout from './Logout';

function AdminJobtypesModify(props) {
    const {
        text,
        transactionTypeId,
        transactionAmount,
        transactionTypeName,
        onNameFieldChange,
        onAmountFieldChange,
        onSaveUpdatedJobType,
    } = props;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/admin/jobtypes">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.modifyJobTypes}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="jobtype" className="col-form-label col-5">{text.chooseJobType}</label>
                        <div className="col-7">
                            <JobtypesBox id="jobtype" className="form-control" />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">{text.modifyNameOfJobType}</label>
                        <div className="col-7">
                            <input id="name" className="form-control" type="text" value={transactionTypeName} onChange={onNameFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">{text.modifyAmountOfJobType}</label>
                        <div className="col-7">
                            <input id="amount" className="form-control" type="text" value={transactionAmount} onChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => onSaveUpdatedJobType({ id: transactionTypeId, transactionTypeName, transactionAmount })}>{text.saveChangesToJobType}</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <Logout/>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        transactionAmount: state.transactionAmount,
        transactionTypeName: state.transactionTypeName,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onNameFieldChange: e => dispatch(MODIFY_TRANSACTION_TYPE_NAME(e.target.value)),
        onAmountFieldChange: e => dispatch(MODIFY_JOB_AMOUNT(e.target.value)),
        onSaveUpdatedJobType: jobtype => dispatch(MODIFY_JOBTYPE_REQUEST(jobtype)),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobtypesModify);
