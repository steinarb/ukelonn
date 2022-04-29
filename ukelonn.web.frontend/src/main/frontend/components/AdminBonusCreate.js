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
            <nav>
                <Link to="/ukelonn/admin/bonuses">
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
                            <DatePicker
                                selected={new Date(bonusStartDate)}
                                dateFormat="yyyy-MM-dd"
                                onChange={d => dispatch(MODIFY_BONUS_START_DATE(d))}
                                onFocus={e => e.target.blur()} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="enddate">{text.endDate}</label>
                        <div>
                            <DatePicker
                                selected={new Date(bonusEndDate)}
                                dateFormat="yyyy-MM-dd"
                                onChange={d => dispatch(MODIFY_BONUS_END_DATE(d))}
                                onFocus={e => e.target.blur()} />
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
