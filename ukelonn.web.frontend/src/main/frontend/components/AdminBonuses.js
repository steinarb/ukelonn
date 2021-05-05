import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';

function AdminBonuses(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, onLogout } = props;

    return (
        <div>
            <nav>
                <Link to="/ukelonn/">
                    &lt;-
                    &nbsp;
                    {text.registerPayment}
                </Link>
                <h1>{text.administrateBonuses}</h1>
                <Locale />
            </nav>
            <div>
                <Link to="/ukelonn/admin/bonuses/modify">
                    {text.modifyBonuses}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/ukelonn/admin/bonuses/create">
                    {text.createNewBonus}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/ukelonn/admin/bonuses/delete">
                    {text.deleteBonuses}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
            </div>
            <br/>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
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

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminBonuses);
