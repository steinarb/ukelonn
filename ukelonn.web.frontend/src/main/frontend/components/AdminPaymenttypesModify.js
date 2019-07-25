import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    UPDATE,
    PAYMENTTYPELIST_REQUEST,
    MODIFY_PAYMENTTYPE_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Paymenttypes from './Paymenttypes';
import Amount from './Amount';

class AdminPaymenttypesModify extends Component {
    componentDidMount() {
        this.props.onPaymenttypeList();
    }

    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let { paymenttypes, paymenttypesMap, transactiontype, onPaymenttypeFieldChange, onNameFieldChange, onAmountFieldChange, onSaveUpdatedPaymentType, onLogout } = this.props;

        const reduceHeaderRowPadding = { padding: '0 0 0 0' };

        return (
            <div>
                <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/admin/paymenttypes">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    Administer betalingstyper
                </Link>
                <header>
                    <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                        <h1>Endre betalingstyper</h1>
                    </div>
                </header>
                <br/>
                <br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="container">
                        <div className="form-group row">
                            <label htmlFor="paymenttype" className="col-form-label col-5">Velg betalingstype</label>
                            <div className="col-7">
                                <Paymenttypes id="paymenttype" className="form-control" paymenttypes={paymenttypes} paymenttypesMap={paymenttypesMap} value={transactiontype.transactionTypeName} onPaymenttypeFieldChange={onPaymenttypeFieldChange} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="amount" className="col-form-label col-5">Endre navn på betalingstype</label>
                            <div className="col-7">
                                <input id="name" className="form-control" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value, transactiontype)} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="amount" className="col-form-label col-5">Endre beløp for betalingstype</label>
                            <div className="col-7">
                                <Amount id="amount" className="form-control" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <div className="col-5"/>
                            <div className="col-7">
                                <button className="btn btn-primary" onClick={() => onSaveUpdatedPaymentType(transactiontype)}>Lagre endringer i betalingstype</button>
                            </div>
                        </div>
                    </div>
                    <br/>
                </form>
                <br/>
                <button className="btn btn-default" onClick={() => onLogout()}>Logout</button>
                <br/>
                <a href="../../../..">Tilbake til topp</a>
            </div>
        );
    };
};

const emptyPaymenttype = {
    id: -1,
    transactionName: '',
    transactionAmount: 0.0
};


const mapStateToProps = state => {
    if (!state.paymenttypes.find((payment) => payment.id === -1)) {
        state.paymenttypes.unshift(emptyPaymenttype);
    }

    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        paymenttypes: state.paymenttypes,
        paymenttypesMap: new Map(state.paymenttypes.map(i => [i.transactionTypeName, i])),
        transactiontype: state.transactiontype,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onPaymenttypeList: () => dispatch(PAYMENTTYPELIST_REQUEST()),
        onPaymenttypeFieldChange: (selectedValue, paymenttypesMap, account, performedpayment) => {
            let paymenttype = paymenttypesMap.get(selectedValue);
            let changedField = {
                transactiontype: {...paymenttype},
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
        onSaveUpdatedPaymentType: (transactiontype) => dispatch(MODIFY_PAYMENTTYPE_REQUEST(transactiontype)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
};

AdminPaymenttypesModify = connect(mapStateToProps, mapDispatchToProps)(AdminPaymenttypesModify);

export default AdminPaymenttypesModify;
