import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    UPDATE_TRANSACTIONTYPE,
    CREATE_JOBTYPE_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import Jobtypes from './Jobtypes';
import Amount from './Amount';

function AdminJobtypesCreate(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, jobtypes, transactiontype, onNameFieldChange, onAmountFieldChange, onSaveUpdatedJobType, onLogout } = props;

    return (
        <div>
            <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/admin/jobtypes">
                <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                &nbsp;
                {text.administrateJobsAndJobTypes}
            </Link>
            <header>
                <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                    <h1>{text.createNewJobType}</h1>
                </div>
                <div>
                    <Locale />
                </div>
            </header>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">{text.nameOfJobType}</label>
                        <div className="col-7">
                            <input id="name" className="form-control" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value, transactiontype)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">{text.amountForJobType}</label>
                        <div className="col-7">
                            <Amount id="amount" className="form-control" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => onSaveUpdatedJobType(transactiontype)}>{text.createNewJobType}</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
};

const emptyJobtype = {
    id: -1,
    transactionName: '',
    transactionAmount: 0.0
};


function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        jobtypes: state.jobtypes,
        transactiontype: state.transactiontype,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onNameFieldChange: (transactionTypeName) => dispatch(UPDATE_TRANSACTIONTYPE({ transactionTypeName })),
        onAmountFieldChange: (transactionAmount) => dispatch(UPDATE_TRANSACTIONTYPE({ transactionAmount })),
        onSaveUpdatedJobType: (transactiontype) => dispatch(CREATE_JOBTYPE_REQUEST(transactiontype)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobtypesCreate);
