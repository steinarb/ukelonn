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
import Jobtypes from './Jobtypes';
import Amount from './Amount';

function AdminJobtypesCreate(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, jobtypes, transactiontype, onNameFieldChange, onAmountFieldChange, onSaveUpdatedJobType, onLogout } = props;

    return (
        <div>
            <h1>{text.createNewJobType}</h1>
            <br/>
            <Link to="/ukelonn/admin/jobtypes">{text.administrateJobsAndJobTypes}</Link>
            <br/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <label htmlFor="amount">{text.nameOfJobType}</label>
                <input id="name" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value, transactiontype)} />
                <br/>
                <label htmlFor="amount">{text.amountForJobType}</label>
                <Amount id="amount" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                <br/>
                <button onClick={() => onSaveUpdatedJobType(transactiontype)}>{text.createNewJobType}</button>
            </form>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}

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
