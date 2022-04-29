import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import {
    MODIFY_BONUS_ENABLED,
    MODIFY_BONUS_ICONURL,
    MODIFY_BONUS_TITLE,
    MODIFY_BONUS_DESCRIPTION,
    MODIFY_BONUS_FACTOR,
    MODIFY_BONUS_START_DATE,
    MODIFY_BONUS_END_DATE,
    CREATE_NEW_BONUS_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';
import Logout from './Logout';

export default function AdminBonusCreate() {
    const text = useSelector(state => state.displayTexts);
    const bonusEnabled = useSelector(state => state.bonusEnabled);
    const bonusIconurl = useSelector(state => state.bonusIconurl);
    const bonusTitle = useSelector(state => state.bonusTitle);
    const bonusDescription = useSelector(state => state.bonusDescription);
    const bonusFactor = useSelector(state => state.bonusFactor);
    const bonusStartDate = useSelector(state => state.bonusStartDate);
    const bonusEndDate = useSelector(state => state.bonusEndDate);
    const dispatch = useDispatch();

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/admin/bonuses">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateBonuses}
                </Link>
                <h1>{text.createNewBonus}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div clasName="row">
                        <div className="col">
                            <div className="form-check">
                                <div className="form-check">
                                    <input
                                        id="enabled"
                                        type="checkbox"
                                        checked={bonusEnabled}
                                        onChange={e => dispatch(MODIFY_BONUS_ENABLED(e.target.checked))} />
                                    <label htmlFor="enabled" className="form-check-label">{text.activated}</label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="iconurl" className="col-form-label col-5">{text.iconURL}</label>
                        <div className="col-7">
                            <input
                                id="iconurl"
                                type="text"
                                value={bonusIconurl}
                                onChange={e => dispatch(MODIFY_BONUS_ICONURL(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="title" className="col-form-label col-5">{text.title}</label>
                        <div className="col-7">
                            <input
                                id="title"
                                type="text"
                                value={bonusTitle}
                                onChange={e => dispatch(MODIFY_BONUS_TITLE(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="description" className="col-form-label col-5">{text.description}</label>
                        <div className="col-7">
                            <input
                                id="description"
                                type="text"
                                value={bonusDescription}
                                onChange={e => dispatch(MODIFY_BONUS_DESCRIPTION(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="bonusfactor" className="col-form-label col-5">{text.bonusFactor}</label>
                        <div className="col-7">
                            <input
                                id="bonusfactor"
                                type="text"
                                pattern="[0-9]?[.]?[0-9]?[0-9]?"
                                value={bonusFactor}
                                onChange={e => dispatch(MODIFY_BONUS_FACTOR(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="startdate" className="col-form-label col-5">{text.startDate}</label>
                        <div className="col-7">
                            <DatePicker
                                selected={new Date(bonusStartDate)}
                                dateFormat="yyyy-MM-dd"
                                onChange={d => dispatch(MODIFY_BONUS_START_DATE(d))}
                                onFocus={e => e.target.blur()} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="enddate" className="col-form-label col-5">{text.endDate}</label>
                        <div className="col-7">
                            <DatePicker
                                selected={new Date(bonusEndDate)}
                                dateFormat="yyyy-MM-dd"
                                onChange={d => dispatch(MODIFY_BONUS_END_DATE(d))}
                                onFocus={e => e.target.blur()} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button
                                onClick={() => dispatch(CREATE_NEW_BONUS_BUTTON_CLICKED())}>
                                {text.createNewBonus}
                            </button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <Logout/>
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}
