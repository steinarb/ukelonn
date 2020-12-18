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
            <Link to="/ukelonn/">
                &lt;-
                &nbsp;
                {parentTitle}
            </Link>
            <header>
                <div>
                    <h1>{text.performedJobsFor} {account.firstName}</h1>
                </div>
                <div>
                    <Locale />
                </div>
            </header>
            <div>
                <table className="table table-bordered">
                    <thead>
                        <tr>
                            <td>{text.date}</td>
                            <td>{text.jobs}</td>
                            <td>{text.amount}</td>
                            <td>{text.paid}</td>
                        </tr>
                    </thead>
                    <tbody>
                        {jobs.map((job) =>
                                  <tr key={job.id}>
                                      <td>{moment(job.transactionTime).format("YYYY-MM-DD")}</td>
                                      <td>{job.name}</td>
                                      <td>{job.transactionAmount}</td>
                                      <td><input type="checkbox" checked={job.paidOut} readOnly={true}/></td>
                                  </tr>
                                 )}
                    </tbody>
                </table>
            </div>
            <br/>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
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
