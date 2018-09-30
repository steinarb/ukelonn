import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import Jobtypes from './Jobtypes';
import Amount from './Amount';

class AdminJobtypesModify extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onJobtypeList();
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let { haveReceivedResponseFromLogin, loginResponse, jobtypes, jobtypesMap, transactiontype, onJobtypeFieldChange, onNameFieldChange, onAmountFieldChange, onSaveUpdatedJobType, onLogout } = this.state;

        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        return (
            <div>
                <h1>Endre jobbtyper</h1>
                <br/>
                <Link to="/ukelonn/admin/jobtypes">Administer jobber og jobbtyper</Link>
                <br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="jobtype">Velg jobbtype</label>
                    <Jobtypes id="jobtype" jobtypes={jobtypes} jobtypesMap={jobtypesMap} value={transactiontype.transactionTypeName} onJobtypeFieldChange={onJobtypeFieldChange} />
                    <br/>
                    <label htmlFor="amount">Endre navn på jobbtype</label>
                    <input id="name" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value, transactiontype)} />
                    <br/>
                    <label htmlFor="amount">Endre beløp for jobbtype</label>
                    <Amount id="amount" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                    <br/>
                    <button onClick={() => onSaveUpdatedJobType(transactiontype)}>Lagre endringer i jobbtype</button>
                </form>
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
            </div>
        );
    };
};

const emptyJobtype = {
    id: -1,
    transactionName: '',
    transactionAmount: 0.0
};


const mapStateToProps = state => {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        jobtypes: state.jobtypes,
        jobtypesMap: state.jobtypesMap,
        transactiontype: state.transactiontype,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onJobtypeList: () => dispatch({ type: 'JOBTYPELIST_REQUEST' }),
        onJobtypeFieldChange: (selectedValue, jobtypesMap, account, performedjob) => {
            let jobtype = jobtypesMap.get(selectedValue);
            let changedField = {
                transactiontype: {...jobtype},
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onNameFieldChange: (formValue, transactiontype) => {
            let changedField = {
                transactiontype: { ...transactiontype, transactionTypeName: formValue }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onAmountFieldChange: (formValue, transactiontype) => {
            let changedField = {
                transactiontype: { ...transactiontype, transactionAmount: formValue }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onSaveUpdatedJobType: (transactiontype) => dispatch({ type: 'MODIFY_JOBTYPE_REQUEST', transactiontype }),
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
    };
};

AdminJobtypesModify = connect(mapStateToProps, mapDispatchToProps)(AdminJobtypesModify);

export default AdminJobtypesModify;
