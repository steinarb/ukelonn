import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
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
    const bonusStartDate = useSelector(state => state.bonusStartDate.split('T')[0]);
    const bonusEndDate = useSelector(state => state.bonusEndDate.split('T')[0]);
    const dispatch = useDispatch();

    return (
        <div>
            <nav>
                <Link to="/admin/bonuses">
                    &lt;-
                    &nbsp;
                    {text.administrateBonuses}
                </Link>
                <h1>{text.createNewBonus}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="enabled">{text.activated}</label>
                        <div>
                            <input
                                id="enabled"
                                type="checkbox"
                                checked={bonusEnabled}
                                onChange={e => dispatch(MODIFY_BONUS_ENABLED(e.target.checked))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="iconurl">{text.iconURL}</label>
                        <div>
                            <input
                                id="iconurl"
                                type="text"
                                value={bonusIconurl}
                                onChange={e => dispatch(MODIFY_BONUS_ICONURL(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="title">{text.title}</label>
                        <div>
                            <input
                                id="title"
                                type="text"
                                value={bonusTitle}
                                onChange={e => dispatch(MODIFY_BONUS_TITLE(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="description">{text.description}</label>
                        <div>
                            <input
                                id="description"
                                type="text"
                                value={bonusDescription}
                                onChange={e => dispatch(MODIFY_BONUS_DESCRIPTION(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="bonusfactor">{text.bonusFactor}</label>
                        <div>
                            <input
                                id="bonusfactor"
                                type="text"
                                pattern="[0-9]?[.]?[0-9]?[0-9]?"
                                value={bonusFactor}
                                onChange={e => dispatch(MODIFY_BONUS_FACTOR(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="startdate">{text.startDate}</label>
                        <div>
                            <input
                                id="startdate"
                                type="date"
                                value={bonusStartDate}
                                onChange={e => dispatch(MODIFY_BONUS_START_DATE(e.target.value))}
                            />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="enddate">{text.endDate}</label>
                        <div>
                            <input
                                id="enddate"
                                type="date"
                                value={bonusEndDate}
                                onChange={e => dispatch(MODIFY_BONUS_END_DATE(e.target.value))}
                            />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
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
