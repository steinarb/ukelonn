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
            <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/">
                <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                &nbsp;
                {parentTitle}
            </Link>
            <header>
                <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                    <h1>Utførte jobber for {account.firstName}</h1>
                </div>
            </header>
            <div className="table-responsive table-sm table-striped">
                <table className="table">
                    <thead>
                        <tr>
                            <th className="transaction-table-col transaction-table-col1">Dato</th>
                            <th className="transaction-table-col transaction-table-col-hide-overflow transaction-table-col2">Jobber</th>
                            <th className="transaction-table-col transaction-table-col3">Bel.</th>
                            <th className="transaction-table-col transaction-table-col4">Bet.</th>
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
            <button className="btn btn-default" onClick={() => onLogout()}>Logout</button>
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
