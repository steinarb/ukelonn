import React from 'react';
import { useSelector } from 'react-redux';
import {
    useGetLoginQuery,
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
} from '../api';
import Locale from './Locale';
import Logout from './Logout';


export default function Unauthorized() {
    const { data: loginResponse = { roles: [] } } = useGetLoginQuery();
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });

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
