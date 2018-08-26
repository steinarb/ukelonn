import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import Jobtypes from './Jobtypes';
import Amount from './Amount';

class AdminJobtypesCreate extends Component {
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

        const reduceHeaderRowPadding = { padding: '0 0 0 0' };
        const reduceArrowIconSize = {marginLeft: '0px', marginRight: '-200px'}; // Compensating for Material Design Lite left arrow icon claiming more space than it requires

        return (
            <div className="mdl-layout mdl-layout--fixed-header">
                <header className="mdl-layout__header">
                    <div className="mdl-layout__header-row" style={reduceHeaderRowPadding}>
                        <Link to="/ukelonn/admin/jobtypes" className="mdl-navigation__link">
                            <i className="material-icons" style={reduceArrowIconSize} >arrow_backward_ios</i>
                            Lag ny jobbtype
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
                                <input id="name" className="stretch-to-fill" type="text" value={transactiontype.transactionTypeName} onChange={(event) => onNameFieldChange(event.target.value, transactiontype)} />
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
        onSaveUpdatedJobType: (transactiontype) => dispatch({ type: 'CREATE_JOBTYPE_REQUEST', transactiontype }),
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
    };
};

AdminJobtypesCreate = connect(mapStateToProps, mapDispatchToProps)(AdminJobtypesCreate);

export default AdminJobtypesCreate;
