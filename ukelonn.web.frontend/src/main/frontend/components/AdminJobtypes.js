import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';

function AdminJobtypes(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, onLogout } = props;

    return (
        <div>
            <nav>
                <Link to="/ukelonn/">
                    &lt;-
                    &nbsp;
                    {text.registerPayment}
                </Link>
                <h1>{text.administrateJobsAndJobTypes}</h1>
                <Locale />
            </nav>
            <br/>
            <div>
                <Link to="/ukelonn/admin/jobtypes/modify">
                    {text.modifyJobTypes}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/ukelonn/admin/jobtypes/create">
                    {text.createNewJobType}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/ukelonn/admin/jobs/delete">
                    {text.deleteJobs}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/ukelonn/admin/jobs/edit">
                    {text.modifyJobs}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
            </div>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../..">{text.returnToTop}</a>
        </div>
    );
}

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
