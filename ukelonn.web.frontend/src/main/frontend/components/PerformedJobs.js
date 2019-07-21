import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { parse } from 'qs';
import moment from 'moment';
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
        let { haveReceivedResponseFromLogin, loginResponse, parentTitle, account, jobs, onLogout } = this.props;
        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

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
                                <td><input type="checkbox" checked={job.paidOut} readOnly="true"/></td>
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
};

const mapStateToProps = state => {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        parentTitle: state.parentTitle,
        account: state.account,
        jobs: state.jobs,
    };
};
const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
        onAccount: (username) => dispatch(ACCOUNT_REQUEST(username)),
        onJobs: (accountId) => dispatch(RECENTJOBS_REQUEST(accountId)),
        onParentTitle: (parentTitle) => dispatch(UPDATE({ parentTitle })),
    };
};

PerformedJobs = connect(mapStateToProps, mapDispatchToProps)(PerformedJobs);

export default PerformedJobs;
