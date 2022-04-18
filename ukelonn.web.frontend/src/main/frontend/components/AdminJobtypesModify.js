import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_JOB_AMOUNT,
    MODIFY_JOBTYPE_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import JobtypesBox from './JobtypesBox';

function AdminJobtypesModify(props) {
    const {
        text,
        transactionTypeId,
        transactionAmount,
        transactionTypeName,
        onNameFieldChange,
        onAmountFieldChange,
        onSaveUpdatedJobType,
        onLogout
    } = props;

    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

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
                            <JobtypesBox id="jobtype" />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">{text.modifyNameOfJobType}</label>
                        <div className="col-7">
                            <input id="name" type="text" value={transactionTypeName} onChange={onNameFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">{text.modifyAmountOfJobType}</label>
                        <div className="col-7">
                            <input id="amount" type="text" value={transactionAmount} onChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button onClick={() => onSaveUpdatedJobType({ id: transactionTypeId, transactionTypeName, transactionAmount })}>{text.saveChangesToJobType}</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>{text.logout}</button>
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
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobtypesModify);
