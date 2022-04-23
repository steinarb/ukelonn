import React from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import Locale from './Locale';
import Logout from './Logout';

export default function AdminPaymenttypes() {
    const text = useSelector(state => state.displayTexts);

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin">
                    &lt;-
                    &nbsp;
                    {text.registerPayment}
                </Link>
                <h1>{text.administratePaymenttypes}</h1>
                <Locale />
            </nav>
            <div>
                <Link to="/ukelonn/admin/paymenttypes/modify">
                    {text.modifyPaymenttypes}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/ukelonn/admin/paymenttypes/create">
                    {text.createPaymenttype}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
            </div>
          <br/>
          <Logout/>
          <br/>
          <a href="../../..">{text.returnToTop}</a>
        </div>
    );
}
