import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';

class AdminJobtypes extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let { haveReceivedResponseFromLogin, loginResponse, onLogout } = this.state;

        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        const reduceHeaderRowPadding = { padding: '0 0 0 0' };
        const reduceArrowIconSize = {marginLeft: '0px', marginRight: '-200px'}; // Compensating for Material Design Lite left arrow icon claiming more space than it requires

        return (
            <div className="mdl-layout mdl-layout--fixed-header">
                <header className="mdl-layout__header">
                    <div className="mdl-layout__header-row" style={reduceHeaderRowPadding}>
                        <Link to="/ukelonn/admin" className="mdl-navigation__link">
                            <i className="material-icons" style={reduceArrowIconSize} >arrow_backward_ios</i>
                            Registrer betaling
                        </Link>
                        <span className="mdl-layout-title">Administrer jobbtyper</span>
                    </div>
                </header>
                <main className="mdl-layout__content">
                    <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to="/ukelonn/admin/jobtypes/modify">
                        Endre jobbtyper
                        <i className="material-icons">arrow_forward_ios</i>
                    </Link>
                    <Link className="mdl-button mdl-js-button mdl-button--raised mdl-navigation__link right-align-cell" to="/ukelonn/admin/jobtypes/create">
                        Lag ny jobbtype
                        <i className="material-icons">arrow_forward_ios</i>
                    </Link>
                </main>
                <br/>
                <br/>
                <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onLogout()}>Logout</button>
            </div>
        );
    };
};

const mapStateToProps = state => {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
    };
};

AdminJobtypes = connect(mapStateToProps, mapDispatchToProps)(AdminJobtypes);

export default AdminJobtypes;
