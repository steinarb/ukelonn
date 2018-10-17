import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import moment from 'moment';
import Accounts from './Accounts';
import Jobtypes from './Jobtypes';

function reloadJobListWhenAccountHasChanged(oldAccount, newAccount, loadJobs) {
    if (oldAccount !== newAccount) {
        loadJobs(newAccount);
    }
}

class AdminJobsEdit extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onAccounts();
        this.props.onJobtypeList();
        this.props.onJobs(this.props.account);
    }

    componentWillReceiveProps(props) {
        reloadJobListWhenAccountHasChanged(this.props.account, props.account, this.props.onJobs);

        this.setState({...props});
    }

    render() {
        let { haveReceivedResponseFromLogin, loginResponse, account, jobs, accounts, accountsMap, jobtypes, jobtypesMap, selectedjob, onLogout, onJobtypeFieldChange, onAccountsFieldChange, onRowClick, onDateFieldChange, onSaveEditedJob } = this.state;

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
                        <span className="mdl-layout-title">Endre jobber for {account.firstName}</span>
                    </div>
                </header>
                <main className="mdl-layout__content">

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
                                <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col1">Dato</td>
                                <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col-hide-overflow transaction-table-col2">Jobber</td>
                                <td className="transaction-table-col transaction-table-col3">Bel.</td>
                            </tr>
                        </thead>
                        <tbody>
                            {jobs.map((job) =>
                                <tr onClick={ ()=>onRowClick(account, job) } key={job.id}>
                                    <td className="mdl-data-table__cell--non-numeric transaction-table-col">{moment(job.transactionTime).format("YYYY-MM-DD")}</td>
                                    <td className="mdl-data-table__cell--non-numeric transaction-table-col transaction-table-col-hide-overflow">{job.name}</td>
                                    <td className="transaction-table-col">{job.transactionAmount}</td>
                                </tr>
                            )}
                        </tbody>
                    </table>

                    <h2>Endre jobb</h2>

                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="jobtype">Jobbtype</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <Jobtypes id="jobtype" jobtypes={jobtypes} jobtypesMap={jobtypesMap} value={selectedjob.transactionType.transactionTypeName} account={account} performedjob={selectedjob} onJobtypeFieldChange={onJobtypeFieldChange} />
                        </div>
                    </div>

                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="amount">Beløp</label>
                         </div>
                         <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <input id="amount" type="text" className='mdl-textfield__input' value={selectedjob.transactionAmount} readOnly="true" />
                         </div>
                    </div>

                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="date">Dato</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <DatePicker selected={selectedjob.transactionTime} dateFormat="YYYY-MM-DD" onChange={(selectedValue) => onDateFieldChange(selectedValue, selectedjob)} readOnly={true} />
                        </div>
                    </div>
                    <button onClick={() => onSaveEditedJob(selectedjob)}>Lagre endret jobb</button>
                </main>
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
        jobtypes: state.jobtypes,
        jobtypesMap: state.jobtypesMap,
        selectedjob: state.selectedjob,
    };
};

const emptyJob = {
    accountId: -1,
    transactionType: { transactionTypeName: '' },
    transactionTypeId: -1,
    transactionAmount: 0.0,
    transactionTime: moment(),
};

const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
        onAccounts: () => dispatch({ type: 'ACCOUNTS_REQUEST' }),
        onJobtypeList: () => dispatch({ type: 'JOBTYPELIST_REQUEST' }),
        onJobs: (account) => dispatch({ type: 'RECENTJOBS_REQUEST', accountId: account.accountId }),
        onAccountsFieldChange: (selectedValue, accountsMap, paymenttype) => {
            let account = accountsMap.get(selectedValue);
            let changedField = {
                account,
                selectedjob: { ...emptyJob },
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onRowClick: (account, job) => {
            const jobtype = job.transactionType;
            let changedField = {
                selectedjob: {
                    ...job,
                    accountId: account.accountId,
                    transactionTypeId: jobtype.id,
                    transactionTime: moment(job.transactionTime),
                },
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onJobtypeFieldChange: (selectedValue, jobtypesMap, account, selectedjob) => {
            let jobtype = jobtypesMap.get(selectedValue);
            let changedField = {
                selectedjob: {
                    ...selectedjob,
                    transactionType: jobtype,
                    transactionTypeId: jobtype.id,
                    transactionAmount: jobtype.transactionAmount,
                }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onDateFieldChange: (selectedValue, selectedjob) => {
            let changedField = {
                selectedjob: {
                    ...selectedjob,
                    transactionTime: selectedValue,
                }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onSaveEditedJob: (selectedjob) => {
            dispatch({ type: 'UPDATE_JOB_REQUEST', selectedjob });
            let changedField = {
                selectedjob: {
                    ...emptyJob,
                    transactionType: { transactionTypeName: '' },
                }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
    };
};

AdminJobsEdit = connect(mapStateToProps, mapDispatchToProps)(AdminJobsEdit);

export default AdminJobsEdit;
