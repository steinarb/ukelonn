import React from 'react';
import { Redirect } from 'react-router';
import { connect } from 'react-redux';
import Locale from './Locale';
import Logout from './Logout';


function Unauthorized(props) {
    const { haveReceivedResponseFromLogin, loginResponse, text } = props;
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
                            <Logout/>
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

export default connect(mapStateToProps)(Unauthorized);
