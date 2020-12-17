import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';

function AdminJobtypes(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, onLogout } = props;

    return (
        <div>
            <h1>{text.administrateJobsAndJobTypes}</h1>
            <br/>
            <Link to="/ukelonn/admin">{text.registerPayment}</Link><br/>
            <Link to="/ukelonn/admin/jobtypes/modify">{text.modifyJobTypes}</Link><br/>
            <Link to="/ukelonn/admin/jobtypes/create">{text.createNewJobType}</Link><br/>
            <Link to="/ukelonn/admin/jobs/delete">{text.deleteJobs}</Link><br/>
            <Link to="/ukelonn/admin/jobs/edit">{text.modifyJobs}</Link><br/>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../..">{text.returnToTop}</a>
        </div>
    );
};

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobtypes);
