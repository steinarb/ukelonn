import React from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import Locale from './Locale';
import Logout from './Logout';

export default function AdminBonuses() {
    const text = useSelector(state => state.displayTexts);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.registerPayment}
                </Link>
                <h1>{text.administrateBonuses}</h1>
                <Locale />
            </nav>
            <div>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/bonuses/modify">
                    {text.modifyBonuses}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/bonuses/create">
                    {text.createNewBonus}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/bonuses/delete">
                    {text.deleteBonuses}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
            </div>
            <br/>
            <br/>
            <Logout />
            <br/>
            <a href="../../..">{text.returnToTop}</a>
        </div>
    );
}
