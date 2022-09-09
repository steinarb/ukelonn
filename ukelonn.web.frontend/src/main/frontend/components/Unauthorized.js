import React from 'react';
import { Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import Locale from './Locale';
import Logout from './Logout';


export default function Unauthorized() {
    const haveReceivedResponseFromLogin = useSelector(state => state.haveReceivedResponseFromLogin);
    const loginResponse = useSelector(state => state.loginResponse);
    const text = useSelector(state => state.displayTexts);

    if (haveReceivedResponseFromLogin && !loginResponse.roles.length) {
        return <Navigate to="/ukelonn/login" />;
    }

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <a className="btn btn-primary left-align-cell" href="./"><span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>&nbsp;{text.goHome}!</a>
                <h1>{text.unauthorized}</h1>
                <Locale />
            </nav>
            <div className="container">
                <p>{text.hello} {loginResponse.username}! {text.youHaveNoAccess}</p>
                <p>{text.click} &quot;{text.goHome}&quot; {text.toNavigateOutOrLogout}</p>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <Logout className="btn btn-primary" />
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}
