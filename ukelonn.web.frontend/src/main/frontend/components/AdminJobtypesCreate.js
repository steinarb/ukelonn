import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    UPDATE_TRANSACTIONTYPE,
    CREATE_JOBTYPE_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Jobtypes from './Jobtypes';
import Amount from './Amount';

function AdminJobtypesCreate(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { jobtypes, jobtypesMap, transactiontype, onNameFieldChange, onAmountFieldChange, onSaveUpdatedJobType, onLogout } = props;

    return (
        <div className="mdl-layout mdl-layout--fixed-header">
            <header className="mdl-layout__header">
                <div className="mdl-layout__header-row" style={reduceHeaderRowPadding}>
                    <Link to="/ukelonn/admin/jobtypes" className="mdl-navigation__link">
                        <i className="material-icons" >chevron_left</i>
                        &nbsp;
                        Administrer jobber og jobbtyper
                    </Link>
                    <span className="mdl-layout-title">Endre jobbtyper</span>
                </div>
            </header>
            <main className="mdl-layout__content">
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="amount">Navn på jobbtype</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <input id="name" className='mdl-textfield__input stretch-to-fill' type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value, transactiontype)} />
                        </div>
                    </div>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="amount">Beløp for jobbtype</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <Amount id="amount" className="stretch-to-fill" payment={transactiontype} onAmountFieldChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--hide-phone mdl-cell--4-col-tablet mdl-cell--8-col-desktop">
                            &nbsp;
                        </div>
                        <div className="mdl-cell mdl-cell--4-col-phone mdl-cell--4-col-tablet mdl-cell--4-col-desktop">
                            <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onSaveUpdatedJobType(transactiontype)}>Lag ny jobbtype</button>
                        </div>
                    </div>
                </form>
            </main>
            <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onLogout()}>Logout</button>
            <br/>
            <a href="../../../..">Tilbake til topp</a>
        </div>
    );
}

const emptyJobtype = {
    id: -1,
    transactionName: '',
    transactionAmount: 0.0
};


function mapStateToProps(state) {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        jobtypes: state.jobtypes,
        transactiontype: state.transactiontype,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onNameFieldChange: (transactionTypeName) => dispatch(UPDATE_TRANSACTIONTYPE({ transactionTypeName })),
        onAmountFieldChange: (transactionAmount) => dispatch(UPDATE_TRANSACTIONTYPE({ transactionAmount })),
        onSaveUpdatedJobType: (transactiontype) => dispatch(CREATE_JOBTYPE_REQUEST(transactiontype)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobtypesCreate);
