import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import Locale from './Locale';
import Logout from './Logout';

function AdminPaymenttypes(props) {
    const { text } = props;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/admin">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.registerPayment}
                </Link>
                <h1>{text.administratePaymenttypes}</h1>
                <Locale />
            </nav>
            <div className="container">
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/paymenttypes/modify">
                    {text.modifyPaymenttypes}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/paymenttypes/create">
                    {text.createPaymenttype}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
            </div>
          <br/>
          <Logout/>
          <br/>
          <a href="../../..">{text.returnToTop}</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
    };
}

export default connect(mapStateToProps)(AdminPaymenttypes);
