import React from 'react';
import { Redirect } from 'react-router';
import { connect } from 'react-redux';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';


function Unauthorized(props) {
    const { haveReceivedResponseFromLogin, loginResponse, text, onLogout } = props;
    if (haveReceivedResponseFromLogin && !loginResponse.roles.length) {
        return <Redirect to="/ukelonn/login" />;
    }

    return (
        <div>
            <nav>
                <a href="./">&lt;&nbsp;{text.goHome}!</a>
                <h1>{text.unauthorized}</h1>
                <Locale />
            </nav>
            <div>
                <p>{text.hello} {loginResponse.username}! {text.youHaveNoAccess}</p>
                <p>{text.click} &quot;{text.goHome}&quot; {text.toNavigateOutOrLogout}</p>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div>
                        <div/>
                        <div>
                            <button onClick={onLogout}>{text.logout}</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        text: state.displayTexts,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Unauthorized);
