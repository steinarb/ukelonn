import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    JOBTYPELIST_REQUEST,
    UPDATE,
    CREATE_JOBTYPE_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Jobtypes from './Jobtypes';
import Amount from './Amount';

class AdminJobtypesCreate extends Component {
    componentDidMount() {
        this.props.onJobtypeList();
    }

    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let { jobtypes, jobtypesMap, transactiontype, onJobtypeFieldChange, onNameFieldChange, onAmountFieldChange, onSaveUpdatedJobType, onLogout } = this.props;

        return (
            <div>
                <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/admin/jobtypes">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    Administer jobber og jobbtyper
                </Link>
                <header>
                    <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                        <h1>Lag ny jobbtype</h1>
                    </div>
                </header>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="container">
                        <div className="form-group row">
                            <label htmlFor="amount" className="col-form-label col-5">Navn på jobbtype</label>
                            <div className="col-7">
                                <input id="name" className="form-control" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value, transactiontype)} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="amount" className="col-form-label col-5">Beløp for jobbtype</label>
                            <div className="col-7">
                                <Amount id="amount" className="form-control" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <div className="col-5"/>
                            <div className="col-7">
                                <button className="btn btn-primary" onClick={() => onSaveUpdatedJobType(transactiontype)}>Lag ny jobbtype</button>
                            </div>
                        </div>
                    </div>
                </form>
                <br/>
                <button className="btn btn-default" onClick={() => onLogout()}>Logout</button>
                <br/>
                <a href="../../../..">Tilbake til topp</a>
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
        transactiontype: state.transactiontype,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onJobtypeList: () => dispatch(JOBTYPELIST_REQUEST()),
        onJobtypeFieldChange: (selectedValue, jobtypesMap, account, performedjob) => {
            let jobtype = jobtypesMap.get(selectedValue);
            let changedField = {
                transactiontype: {...jobtype},
            };
            dispatch(UPDATE(changedField));
        },
        onNameFieldChange: (formValue, transactiontype) => {
            let changedField = {
                transactiontype: { ...transactiontype, transactionTypeName: formValue }
            };
            dispatch(UPDATE(changedField));
        },
        onAmountFieldChange: (formValue, transactiontype) => {
            let changedField = {
                transactiontype: { ...transactiontype, transactionAmount: formValue }
            };
            dispatch(UPDATE(changedField));
        },
        onSaveUpdatedJobType: (transactiontype) => dispatch(CREATE_JOBTYPE_REQUEST(transactiontype)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
};

AdminJobtypesCreate = connect(mapStateToProps, mapDispatchToProps)(AdminJobtypesCreate);

export default AdminJobtypesCreate;
