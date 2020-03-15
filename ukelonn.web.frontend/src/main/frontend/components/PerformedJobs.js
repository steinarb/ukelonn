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

    const reduceHeaderRowPadding = { padding: '0 0 0 0' };
    let { account, jobs, onLogout } = props;
    let queryParams = parse(props.location.search, { ignoreQueryPrefix: true });
    const { parentTitle } = queryParams;

    return (
        <div className="mdl-layout mdl-layout--fixed-header">
            <header className="mdl-layout__header">
                <div className="mdl-layout__header-row" style={reduceHeaderRowPadding}>
                    <Link to="/ukelonn/" className="mdl-navigation__link">
                        <i className="material-icons" >chevron_left</i>
                        &nbsp;
                        {parentTitle}</Link>
                    <span className="mdl-layout-title">Siste jobber</span>
                </div>
            </header>
            <main className="mdl-layout__content">
                <table className="mdl-data-table mdl-js-data-table transaction-table">
                    <thead>
                        <tr>
                            <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col1">Dato</td>
                            <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col-hide-overflow transaction-table-col2">Jobber</td>
                            <td className="transaction-table-col transaction-table-col3">Bel.</td>
                            <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col4">Bet.</td>
                        </tr>
                    </thead>
                    <tbody>
                        {jobs.map((job) =>
                            <tr key={job.id}>
                                <td className="mdl-data-table__cell--non-numeric transaction-table-col">{moment(job.transactionTime).format("YYYY-MM-DD")}</td>
                                <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col-hide-overflow">{job.name}</td>
                                <td className="transaction-table-col">{job.transactionAmount}</td>
                                <td className="mdl-data-table__cell--non-numeric transaction-table-col"><input type="checkbox" checked={job.paidOut} readOnly="true"/></td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </main>
            <br/>
            <br/>
            <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onLogout()}>Logout</button>
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
