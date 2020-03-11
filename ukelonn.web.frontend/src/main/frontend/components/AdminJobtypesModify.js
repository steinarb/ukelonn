import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    UPDATE_TRANSACTIONTYPE,
    MODIFY_JOBTYPE_REQUEST,
} from '../actiontypes';
import JobtypesBox from './JobtypesBox';
import Amount from './Amount';

function AdminJobtypesModify(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { jobtypes, jobtypesMap, transactiontype, onJobtypeFieldChange, onNameFieldChange, onAmountFieldChange, onSaveUpdatedJobType, onLogout } = props;

    return (
        <div>
            <h1>Endre jobbtyper</h1>
            <br/>
            <Link to="/ukelonn/admin/jobtypes">Administer jobber og jobbtyper</Link>
            <br/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <label htmlFor="jobtype">Velg jobbtype</label>
                <JobtypesBox id="jobtype" jobtypes={jobtypes} value={transactiontype.id} onJobtypeFieldChange={onJobtypeFieldChange} />
                <br/>
                <label htmlFor="amount">Endre navn på jobbtype</label>
                <input id="name" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value, transactiontype)} />
                <br/>
                <label htmlFor="amount">Endre beløp for jobbtype</label>
                <Amount id="amount" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                <br/>
                <button onClick={() => onSaveUpdatedJobType(transactiontype)}>Lagre endringer i jobbtype</button>
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
        jobtypesMap: state.jobtypesMap,
        transactiontype: state.transactiontype,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onJobtypeFieldChange: (selectedValue, jobtypes) => {
            const selectedValueInt = parseInt(selectedValue, 10);
            let jobtype = jobtypes.find(jobtype => jobtype.id === selectedValueInt);
            dispatch(UPDATE_TRANSACTIONTYPE({ ...jobtype }));
        },
        onNameFieldChange: (transactionTypeName) => dispatch(UPDATE_TRANSACTIONTYPE({ transactionTypeName })),
        onAmountFieldChange: (transactionAmount) => dispatch(UPDATE_TRANSACTIONTYPE({ transactionAmount })),
        onSaveUpdatedJobType: (transactiontype) => dispatch(MODIFY_JOBTYPE_REQUEST(transactiontype)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobtypesModify);
