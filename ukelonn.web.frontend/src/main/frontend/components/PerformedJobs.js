import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { parse } from 'qs';
import moment from 'moment';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';

function PerformedJobs(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, account, jobs, onLogout } = props;
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
                <h1>{text.performedJobsFor} {account.firstName}</h1>
                <Locale />
            </nav>
            <div className="table-responsive table-sm table-striped">
                <table className="table">
                    <thead>
                        <tr>
                            <th className="transaction-table-col transaction-table-col1">{text.date}</th>
                            <th className="transaction-table-col transaction-table-col-hide-overflow transaction-table-col2">{text.jobs}</th>
                            <th className="transaction-table-col transaction-table-col3">{text.amount}</th>
                            <th className="transaction-table-col transaction-table-col4">{text.paid}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {jobs.map((job) =>
                             <tr key={job.id}>
                                 <td className="transaction-table-col">{moment(job.transactionTime).format("YYYY-MM-DD")}</td>
                                 <td className="transaction-table-col transaction-table-col-hide-overflow">{job.name}</td>
                                 <td className="transaction-table-col">{job.transactionAmount}</td>
                                 <td className="transaction-table-col"><input type="checkbox" checked={job.paidOut} readOnly={true}/></td>
                             </tr>
                         )}
                    </tbody>
                </table>
            </div>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>{text.logout}</button>
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
        account: state.account,
        jobs: state.jobs,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(PerformedJobs);
