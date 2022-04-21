import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { parse } from 'qs';
import Locale from './Locale';
import Logout from './Logout';

function PerformedJobs(props) {
    const {
        text,
        accountFirstname,
        jobs,
    } = props;
    let queryParams = parse(props.location.search, { ignoreQueryPrefix: true });
    const { parentTitle } = queryParams;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {parentTitle}
                </Link>
                <h1>{text.performedJobsFor} {accountFirstname}</h1>
                <Locale />
            </nav>
            <div className="table-responsive table-sm table-striped">
                <table className="table">
                    <thead>
                        <tr>
                            <th className="transaction-table-col1">{text.date}</th>
                            <th className="transaction-table-col-hide-overflow transaction-table-col2">{text.jobs}</th>
                            <th className="transaction-table-col-clip-overflow transaction-table-col3">{text.amount}</th>
                            <th className="transaction-table-col-clip-overflow transaction-table-col4">{text.paid}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {jobs.map((job) =>
                            <tr key={job.id}>
                                <td>{new Date(job.transactionTime).toISOString().split('T')[0]}</td>
                                <td className="transaction-table-col-hide-overflow">{job.name}</td>
                                <td>{job.transactionAmount}</td>
                                <td><input type="checkbox" checked={job.paidOut} readOnly={true}/></td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
            <br/>
            <Logout />
            <br/>
            <a href="../..">{text.returnToTop}</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        parentTitle: state.parentTitle,
        accountFirstname: state.accountFirstname,
        jobs: state.jobs,
    };
}

export default connect(mapStateToProps)(PerformedJobs);
