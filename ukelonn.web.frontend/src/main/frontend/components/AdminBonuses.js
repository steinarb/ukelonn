import React from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import Locale from './Locale';
import Logout from './Logout';

export default function AdminBonuses() {
    const text = useSelector(state => state.displayTexts);

    return (
        <div>
            <nav>
                <Link to="/">
                    &lt;-
                    &nbsp;
                    {text.registerPayment}
                </Link>
                <h1>{text.administrateBonuses}</h1>
                <Locale />
            </nav>
            <div>
                <Link to="/admin/bonuses/modify">
                    {text.modifyBonuses}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/admin/bonuses/create">
                    {text.createNewBonus}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
                <Link to="/admin/bonuses/delete">
                    {text.deleteBonuses}
                    &nbsp;
                    -&gt;
                </Link>
                <br/>
            </div>
            <br/>
            <br/>
            <Logout />
            <br/>
            <a href="../../..">{text.returnToTop}</a>
        </div>
    );
}
