import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { parse } from 'qs';

class PerformedJobs extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        let { account } = this.props;
        let queryParams = parse(this.props.location.search, { ignoreQueryPrefix: true });
        const accountId = account.firstName === "Ukjent" ? queryParams.accountId : account.accountId;
        this.props.onJobs(accountId);

        if (account.firstName === "Ukjent" && queryParams.username) {
            this.props.onAccount(queryParams.username);
        }
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let { haveReceivedResponseFromLogin, loginResponse, account, jobs, onLogout } = this.state;
        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        return (
            <div>
                <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    Register betaling
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
                                     <td className="transaction-table-col">{job.transactionTime}</td>
                                     <td className="transaction-table-col transaction-table-col-hide-overflow">{job.name}</td>
                                     <td className="transaction-table-col">{job.transactionAmount}</td>
                                     <td className="transaction-table-col"><input type="checkbox" checked={job.paidOut}/></td>
                                 </tr>
                             )}
                        </tbody>
                    </table>
                </div>
                <br/>
                <button className="btn btn-default" onClick={() => onLogout()}>Logout</button>
            </div>
        );
    }
};

const mapStateToProps = state => {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        account: state.account,
        jobs: state.jobs,
    };
};
const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
        onAccount: (username) => dispatch({ type: 'ACCOUNT_REQUEST', username }),
        onJobs: (accountId) => dispatch({ type: 'RECENTJOBS_REQUEST', accountId: accountId }),
    };
};

PerformedJobs = connect(mapStateToProps, mapDispatchToProps)(PerformedJobs);

export default PerformedJobs;
