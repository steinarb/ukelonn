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

        return (
            <div>
                <h1>Administrer jobber og jobbtyper</h1>
                <br/>
                <Link to="/ukelonn/admin">Registrer betaling</Link><br/>
                <Link to="/ukelonn/admin/jobtypes/modify">Endre jobbtyper</Link><br/>
                <Link to="/ukelonn/admin/jobtypes/create">Lag ny jobbtype</Link><br/>
                <Link to="/ukelonn/admin/jobs/delete">Slett jobber</Link><br/>
                <Link to="/ukelonn/admin/jobs/edit">Endre jobber</Link><br/>
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
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
