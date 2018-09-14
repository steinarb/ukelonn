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

        const reduceHeaderRowPadding = { padding: '0 0 0 0' };

        return (
            <div className="mdl-layout mdl-layout--fixed-header">
                <header className="mdl-layout__header">
                    <div className="mdl-layout__header-row" style={reduceHeaderRowPadding}>
                        <Link to="/ukelonn/admin/jobtypes" className="mdl-navigation__link">
                            <i className="material-icons" >chevron_left</i>
                            &nbsp;
                            Administrer jobber og jobbtyper
                        </Link>
                        <span className="mdl-layout-title">Slett jobber</span>
                    </div>
                </header>
                <main className="mdl-layout__content">

                    <p><em>Merk!</em> Det er bare feilregistreringer som skal slettes!<br/>
                        <em>Ikke</em> slett jobber som skal utbetales</p>

                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="account-selector">Velg konto:</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <Accounts  id="account-selector" accounts={accounts} accountsMap={accountsMap} account={account} onAccountsFieldChange={onAccountsFieldChange}/>
                        </div>
                    </div>
                    <table className="mdl-data-table mdl-js-data-table transaction-table">
                        <thead>
                            <tr>
                                <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col4">Slett</td>
                                <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col1">Dato</td>
                                <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col-hide-overflow transaction-table-col2">Jobber</td>
                                <td className="transaction-table-col transaction-table-col3">Bel.</td>
                            </tr>
                        </thead>
                        <tbody>
                            {jobs.map((job) =>
                                <tr key={job.id}>
                                    <td className="mdl-data-table__cell--non-numeric transaction-table-col"><input type="checkbox" checked={job.delete} onChange={(e) => onCheckboxTicked(e.target.checked, job, jobs)}/></td>
                                    <td className="mdl-data-table__cell--non-numeric transaction-table-col">{job.transactionTime}</td>
                                    <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col-hide-overflow">{job.name}</td>
                                    <td className="transaction-table-col">{job.transactionAmount}</td>
                                 </tr>
                            )}
                        </tbody>
                    </table>
                    <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onDeleteMarkedJobs(account, jobs)}>Slett merkede jobber</button>
                </main>
                <br/>
                <br/>
                <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onLogout()}>Logout</button>
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
        onJobs: (account) => dispatch({ type: 'RECENTJOBS_REQUEST', account: account }),
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
