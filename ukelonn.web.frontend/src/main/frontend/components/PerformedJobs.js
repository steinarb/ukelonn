import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { parse } from 'qs';
import moment from 'moment';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    ACCOUNT_REQUEST,
    RECENTJOBS_REQUEST,
    UPDATE,
} from '../actiontypes';

class PerformedJobs extends Component {
    componentDidMount() {
        let { account } = this.props;
        let queryParams = parse(this.props.location.search, { ignoreQueryPrefix: true });
        const accountId = account.firstName === 'Ukjent' ? queryParams.accountId : account.accountId;
        this.props.onJobs(accountId);
        const parentTitle = queryParams.parentTitle ? queryParams.parentTitle : 'Register betaling';
        this.props.onParentTitle(parentTitle);

        if (account.firstName === 'Ukjent' && queryParams.username) {
            this.props.onAccount(queryParams.username);
        }
    }

    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let { account, jobs, onLogout } = this.props;
        let queryParams = parse(this.props.location.search, { ignoreQueryPrefix: true });
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
                                     <td className="transaction-table-col"><input type="checkbox" checked={job.paidOut} readOnly="true"/></td>
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
};

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
        onAccount: (username) => dispatch(ACCOUNT_REQUEST(username)),
        onJobs: (accountId) => dispatch(RECENTJOBS_REQUEST(accountId)),
        onParentTitle: (parentTitle) => dispatch(UPDATE({ parentTitle })),
    };
}

PerformedJobs = connect(mapStateToProps, mapDispatchToProps)(PerformedJobs);

export default PerformedJobs;
