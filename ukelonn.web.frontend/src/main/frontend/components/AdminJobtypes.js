import React from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import Locale from './Locale';
import Logout from './Logout';

export default function AdminJobtypes() {
    const text = useSelector(state => state.displayTexts);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.registerPayment}
                </Link>
                <h1>{text.administrateJobsAndJobTypes}</h1>
                <Locale />
            </nav>
            <div className="container">
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/jobtypes/modify">
                    {text.modifyJobTypes}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/jobtypes/create">
                    {text.createNewJobType}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/jobs/delete">
                    {text.deleteJobs}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/jobs/edit">
                    {text.modifyJobs}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
            </div>
            <br/>
            <br/>
            <Logout />
            <br/>
            <a href="../../..">{text.returnToTop}</a>
        </div>
    );
}
