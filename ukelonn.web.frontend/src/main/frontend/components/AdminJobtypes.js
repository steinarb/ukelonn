import React from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import Locale from './Locale';
import Logout from './Logout';

export default function AdminJobtypes() {
    const text = useSelector(state => state.displayTexts);

    return (
        <div>
            <nav>
                <Link to="/">
                    &lt;-
                    &nbsp;
                    {text.registerPayment}
                </Link>
                <h1>{text.administrateJobsAndJobTypes}</h1>
                <Locale />
            </nav>
            <br/>
            <div>
                <Link to="/admin/jobtypes/modify">
                    {text.modifyJobTypes}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/admin/jobtypes/create">
                    {text.createNewJobType}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/admin/jobs/delete">
                    {text.deleteJobs}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/admin/jobs/edit">
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
