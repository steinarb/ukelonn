import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import {
    SELECT_BONUS,
    DELETE_SELECTED_BONUS_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import Logout from './Logout';

export default function AdminBonusesDelete() {
    const text = useSelector(state => state.displayTexts);
    const allbonuses = useSelector(state => state.allbonuses);
    const bonusId = useSelector(state => state.bonusId);
    const bonusTitle = useSelector(state => state.bonusTitle);
    const bonusDescription = useSelector(state => state.bonusDescription);
    const dispatch = useDispatch();

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/admin/bonuses">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateBonuses}
                </Link>
                <h1>{text.deleteBonuses}</h1>
                <Locale />
            </nav>

            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="bonus" className="col-form-label col-5">{text.chooseBonus}</label>
                        <div className="col-7">
                            <select id="bonus" value={bonusId} onChange={e => dispatch(SELECT_BONUS(parseInt(e.target.value)))}>
                                <option key="-1" value="-1" />
                                {allbonuses.map(b => <option key={b.bonusId} value={b.bonusId}>{b.title}</option>)}
                            </select>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="title" className="col-form-label col-5">{text.title}</label>
                        <div className="col-7">
                            <input readOnly id="title" className="form-control" type="text" value={bonusTitle} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="description" className="col-form-label col-5">{text.description}</label>
                        <div className="col-7">
                            <input readOnly id="description" className="form-control" type="text" value={bonusDescription} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button onClick={() => dispatch(DELETE_SELECTED_BONUS_BUTTON_CLICKED())}>{text.deleteSelectedBonus}</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <Logout />
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}
