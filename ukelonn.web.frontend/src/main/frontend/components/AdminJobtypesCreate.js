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

    let { jobtypes, transactiontype, onNameFieldChange, onAmountFieldChange, onSaveUpdatedJobType, onLogout } = props;

    return (
        <div>
            <h1>Lag ny jobbtype</h1>
            <br/>
            <Link to="/ukelonn/admin/jobtypes">Administer jobber og jobbtyper</Link>
            <br/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <label htmlFor="amount">Navn på jobbtype</label>
                <input id="name" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value, transactiontype)} />
                <br/>
                <label htmlFor="amount">Beløp for jobbtype</label>
                <Amount id="amount" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                <br/>
                <button onClick={() => onSaveUpdatedJobType(transactiontype)}>Lag ny jobbtype</button>
            </form>
            <br/>
            <button onClick={() => onLogout()}>Logout</button>
            <br/>
            <a href="../../../..">Tilbake til topp</a>
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
