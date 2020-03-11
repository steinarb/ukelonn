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

function PerformedJobs(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { account, jobs, onLogout } = props;
    let queryParams = parse(props.location.search, { ignoreQueryPrefix: true });
    const { parentTitle } = queryParams;

    return (
        <div>
            <Link to="/ukelonn/">{parentTitle}</Link>
            <br/>
            <h1>Utførte jobber for {account.firstName}</h1>
            <table className="table table-bordered">
                <thead>
                    <tr>
                        <td>Dato</td>
                        <td>Jobber</td>
                        <td>Beløp</td>
                        <td>Utbetalt</td>
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
            <br/>
            <br/>
            <button onClick={() => onLogout()}>Logout</button>
            <br/>
            <a href="../..">Tilbake til topp</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
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
