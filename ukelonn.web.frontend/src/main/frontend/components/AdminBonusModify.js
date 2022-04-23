import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import DatePicker from 'react-datepicker';
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

function AdminBonusesModify(props) {
    const {
        text,
        allbonuses,
        bonusId,
        bonusEnabled,
        bonusIconurl,
        bonusTitle,
        bonusDescription,
        bonusFactor,
        bonusStartDate,
        bonusEndDate,
    } = props;
    const dispatch = useDispatch();

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/bonuses">
                    &lt;-
                    &nbsp;
                    {text.administrateBonuses}
                </Link>
                <h1>{text.modifyBonuses}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="bonus">{text.chooseBonus}</label>
                        <div>
                            <select id="bonus" value={bonusId} onChange={e => dispatch(SELECT_BONUS(parseInt(e.target.value)))}>
                                <option key="-1" value="-1" />
                                {allbonuses.map(b => <option key={b.bonusId} value={b.bonusId}>{b.title}</option>)}
                            </select>
                        </div>
                    </div>
                    <div>
                        <label htmlFor="enabled">{text.activated}</label>
                        <div>
                            <input id="enabled" type="checkbox" checked={bonusEnabled} onChange={e => dispatch(MODIFY_BONUS_ENABLED(e.target.checked))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="iconurl">{text.iconURL}</label>
                        <div>
                            <input id="iconurl" type="text" value={bonusIconurl} onChange={e => dispatch(MODIFY_BONUS_ICONURL(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="title">{text.title}</label>
                        <div>
                            <input id="title" type="text" value={bonusTitle} onChange={e => dispatch(MODIFY_BONUS_TITLE(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="description">{text.description}</label>
                        <div>
                            <input id="description" type="text" value={bonusDescription} onChange={e => dispatch(MODIFY_BONUS_DESCRIPTION(e.target.value))} />
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
                            <button onClick={() => dispatch(SAVE_BONUS_CHANGES_BUTTON_CLICKED())}>{text.saveChangesToBonus}</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <Logout />
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        allbonuses: state.allbonuses,
        bonusId: state.bonusId,
        bonusEnabled: state.bonusEnabled,
        bonusIconurl: state.bonusIconurl,
        bonusTitle: state.bonusTitle,
        bonusDescription: state.bonusDescription,
        bonusFactor: state.bonusFactor,
        bonusStartDate: state.bonusStartDate,
        bonusEndDate: state.bonusEndDate,
    };
}

export default connect(mapStateToProps)(AdminBonusesModify);
