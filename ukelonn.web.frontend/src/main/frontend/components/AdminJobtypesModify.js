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

    let { text, jobtypes, transactiontype, onJobtypeFieldChange, onNameFieldChange, onAmountFieldChange, onSaveUpdatedJobType, onLogout } = props;

    return (
        <div>
            <h1>{text.modifyJobTypes}</h1>
            <br/>
            <Link to="/ukelonn/admin/jobtypes">{text.administrateJobsAndJobTypes}</Link>
            <br/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <label htmlFor="jobtype">{text.chooseJobType}</label>
                <JobtypesBox id="jobtype" jobtypes={jobtypes} value={transactiontype.id} onJobtypeFieldChange={onJobtypeFieldChange} />
                <br/>
                <label htmlFor="amount">{text.modifyNameOfJobType}</label>
                <input id="name" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value, transactiontype)} />
                <br/>
                <label htmlFor="amount">{text.modifyAmountOfJobType}</label>
                <Amount id="amount" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                <br/>
                <button onClick={() => onSaveUpdatedJobType(transactiontype)}>{text.saveChangesToJobType}</button>
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
