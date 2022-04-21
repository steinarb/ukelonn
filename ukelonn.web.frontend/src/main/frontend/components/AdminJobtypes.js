import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import Locale from './Locale';
import Logout from './Logout';

function AdminJobtypes(props) {
    let { text } = props;

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
            <Logout />
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

export default connect(mapStateToProps)(AdminJobtypes);
