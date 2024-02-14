import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import {
    SELECT_BONUS,
    MODIFY_BONUS_ENABLED,
    MODIFY_BONUS_ICONURL,
    MODIFY_BONUS_TITLE,
    MODIFY_BONUS_DESCRIPTION,
    MODIFY_BONUS_FACTOR,
    MODIFY_BONUS_START_DATE,
    MODIFY_BONUS_END_DATE,
    SAVE_BONUS_CHANGES_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import Logout from './Logout';

export default function AdminBonusesModify() {
    const text = useSelector(state => state.displayTexts);
    const allbonuses = useSelector(state => state.allbonuses);
    const bonusId = useSelector(state => state.bonusId);
    const bonusEnabled = useSelector(state => state.bonusEnabled);
    const bonusIconurl = useSelector(state => state.bonusIconurl);
    const bonusTitle = useSelector(state => state.bonusTitle);
    const bonusDescription = useSelector(state => state.bonusDescription);
    const bonusFactor = useSelector(state => state.bonusFactor);
    const bonusStartDate = useSelector(state => state.bonusStartDate.split('T')[0]);
    const bonusEndDate = useSelector(state => state.bonusEndDate.split('T')[0]);
    const dispatch = useDispatch();

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/admin/bonuses">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateBonuses}
                </Link>
                <h1>{text.modifyBonuses}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row mb-2">
                        <label htmlFor="bonus" className="col-form-label col-5">{text.chooseBonus}</label>
                        <div className="col-7">
                            <select id="bonus" className="form-control" value={bonusId} onChange={e => dispatch(SELECT_BONUS(parseInt(e.target.value)))}>
                                <option key="-1" value="-1" />
                                {allbonuses.map(b => <option key={b.bonusId} value={b.bonusId}>{b.title}</option>)}
                            </select>
                        </div>
                    </div>
                    <div className="row">
                        <div className="col">
                            <div className="form-check">
                            <input
                                id="enabled"
                                className="form-check-input"
                                type="checkbox"
                                checked={bonusEnabled}
                                onChange={e => dispatch(MODIFY_BONUS_ENABLED(e.target.checked))} />
                                <label htmlFor="enabled" className="form-check-label">{text.activated}</label>
                            </div>
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="iconurl" className="col-form-label col-5">{text.iconURL}</label>
                        <div className="col-7">
                            <input
                                id="iconurl"
                                className="form-control"
                                type="text"
                                value={bonusIconurl}
                                onChange={e => dispatch(MODIFY_BONUS_ICONURL(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="title" className="col-form-label col-5">{text.title}</label>
                        <div className="col-7">
                            <input
                                id="title"
                                className="form-control"
                                type="text"
                                value={bonusTitle}
                                onChange={e => dispatch(MODIFY_BONUS_TITLE(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="description" className="col-form-label col-5">{text.description}</label>
                        <div className="col-7">
                            <input
                                id="description"
                                className="form-control"
                                type="text"
                                value={bonusDescription}
                                onChange={e => dispatch(MODIFY_BONUS_DESCRIPTION(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="bonusfactor" className="col-form-label col-5">{text.bonusFactor}</label>
                        <div className="col-7">
                            <input
                                id="bonusfactor"
                                className="form-control"
                                type="text"
                                pattern="[0-9]?[.]?[0-9]?[0-9]?"
                                value={bonusFactor}
                                onChange={e => dispatch(MODIFY_BONUS_FACTOR(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="startdate" className="col-form-label col-5">{text.startDate}</label>
                        <div className="col-7">
                            <input
                                id="startdate"
                                className="form-control"
                                type="date"
                                value={bonusStartDate}
                                onChange={e => dispatch(MODIFY_BONUS_START_DATE(e.target.value))}
                            />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="enddate" className="col-form-label col-5">{text.endDate}</label>
                        <div className="col-7">
                            <input
                                id="enddate"
                                className="form-control"
                                type="date"
                                value={bonusEndDate}
                                onChange={e => dispatch(MODIFY_BONUS_END_DATE(e.target.value))}
                            />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button
                                className="btn btn-primary"
                                onClick={() => dispatch(SAVE_BONUS_CHANGES_BUTTON_CLICKED())}>
                                {text.saveChangesToBonus}
                            </button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <Logout />
        </div>
    );
}
