import React from 'react';
import { useSelector } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
} from '../api';
import { Link } from 'react-router';
import Locale from './Locale';
import Logout from './Logout';

export default function AdminUsers() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });

    return (
        <div>
            <nav>
                <Link to="/admin">
                    &lt;-
                    &nbsp;
                    {text.registerPayment}
                </Link>
                <h1>{text.administrateUsers}</h1>
                <Locale />
            </nav>
            <div>
                <Link to="/admin/users/modify">
                    {text.modifyUsers}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/admin/users/password">
                    {text.changeUsersPassword}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/admin/users/create">
                    {text.addUser}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
            </div>
            <Logout />
            <br/>
            <a href="../../..">{text.returnToTop}</a>
        </div>
    );
}
