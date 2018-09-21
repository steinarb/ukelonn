import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import Accounts from './Accounts';

function reloadJobListWhenAccountHasChanged(oldAccount, newAccount, loadJobs) {
    if (oldAccount !== newAccount) {
        loadJobs(newAccount);
    }
}

class AdminJobsDelete extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onJobs(this.props.account);
    }

    componentWillReceiveProps(props) {
        reloadJobListWhenAccountHasChanged(this.props.account, props.account, this.props.onJobs);

        this.setState({...props});
    }

    render() {
        let { haveReceivedResponseFromLogin, loginResponse, account, jobs, accounts, accountsMap, onLogout, onAccountsFieldChange, onCheckboxTicked, onDeleteMarkedJobs } = this.state;

        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        return (
            <div>
                <Link to="/ukelonn/admin/jobtypes">Administer jobber og jobbtyper</Link>
                <br/>
                <h1>Slett feilregisterte jobber for {account.firstName}</h1>

                <p><em>Merk!</em> Det er bare feilregistreringer som skal slettes!<br/>
                   <em>Ikke</em> slett jobber som skal utbetales</p>
                <label htmlFor="account-selector">Velg konto:</label>
                <Accounts  id="account-selector" accounts={accounts} accountsMap={accountsMap} account={account} onAccountsFieldChange={onAccountsFieldChange}/>

                <table className="table table-bordered">
                    <thead>
                        <tr>
                            <td>Slett</td>
                            <td>Dato</td>
                            <td>Jobber</td>
                            <td>Beløp</td>
                        </tr>
                    </thead>
                    <tbody>
                        {jobs.map((job) =>
                            <tr key={job.id}>
                                <td><input type="checkbox" checked={job.delete} onChange={(e) => onCheckboxTicked(e.target.checked, job, jobs)}/></td>
                                <td>{job.transactionTime}</td>
                                <td>{job.name}</td>
                                <td>{job.transactionAmount}</td>
                            </tr>
                        )}
                    </tbody>
                </table>
                <button onClick={() => onDeleteMarkedJobs(account, jobs)}>Slett merkede jobber</button>
                <br/>
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
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
        accounts: state.accounts,
        accountsMap: state.accountsMap,
    };
};
const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
        onJobs: (account) => dispatch({ type: 'RECENTJOBS_REQUEST', accountId: account.accountId }),
        onAccountsFieldChange: (selectedValue, accountsMap, paymenttype) => {
            let account = accountsMap.get(selectedValue);
            let changedField = {
                account,
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onCheckboxTicked: (deleteChecked, job, jobs) => {
            job.delete = deleteChecked;
            let changedField = {
                jobs: [...jobs],
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onDeleteMarkedJobs: (account, jobs) => {
            const jobsToDelete = jobs.filter(job => job.delete);
            dispatch({ type: 'DELETE_JOBS_REQUEST', account, jobsToDelete });
        },
    };
};

AdminJobsDelete = connect(mapStateToProps, mapDispatchToProps)(AdminJobsDelete);

export default AdminJobsDelete;
