import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    UPDATE_BONUS,
    MODIFY_BONUS,
} from '../actiontypes';
import Locale from './Locale';
import { emptyBonus } from '../constants';

function AdminBonusesModify(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let {
        text,
        allbonuses,
        bonus,
        onUpdateBonus,
        onUpdateEnabled,
        onUpdateIconurl,
        onUpdateTitle,
        onUpdateDescription,
        onUpdateBonusFactor,
        onUpdateStartDate,
        onUpdateEndDate,
        onModifyBonus,
        onLogout,
    } = props;
    const bonuses = [emptyBonus].concat(allbonuses);
    const bonusId = bonus.bonusId;
    const enabled = bonus.enabled;
    const iconurl = bonus.iconurl || '';
    const title = bonus.title || '';
    const description = bonus.description || '';
    const bonusFactor = bonus.bonusFactor || 0;
    const startDate = new Date(bonus.startDate).toISOString();
    const endDate = new Date(bonus.endDate).toISOString();

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
                            <select id="bonus" value={bonusId} onChange={e => onUpdateBonus(bonuses.find(b => b.bonusId === parseInt(e.target.value)))}>
                                {bonuses.map(b => <option key={b.bonusId} value={b.bonusId}>{b.title}</option>)}
                            </select>
                        </div>
                    </div>
                    <div>
                        <label htmlFor="enabled">{text.activated}</label>
                        <div>
                            <input id="enabled" type="checkbox" checked={enabled} onChange={e => onUpdateEnabled(bonus, e)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="iconurl">{text.iconURL}</label>
                        <div>
                            <input id="iconurl" type="text" value={iconurl} onChange={e => onUpdateIconurl(bonus, e)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="title">{text.title}</label>
                        <div>
                            <input id="title" type="text" value={title} onChange={e => onUpdateTitle(bonus, e)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="description">{text.description}</label>
                        <div>
                            <input id="description" type="text" value={description} onChange={e => onUpdateDescription(bonus, e)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="bonusfactor">{text.bonusFactor}</label>
                        <div>
                            <input id="bonusfactor" type="text" pattern="[0-9]?[.]?[0-9]?[0-9]?" value={bonusFactor} onChange={e => onUpdateBonusFactor(bonus, e)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="startdate">{text.startDate}</label>
                        <div>
                            <DatePicker selected={new Date(startDate)} dateFormat="yyyy-MM-dd" onChange={d => onUpdateStartDate(bonus, d)} onFocus={e => e.target.blur()} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="enddate">{text.endDate}</label>
                        <div>
                            <DatePicker selected={new Date(endDate)} dateFormat="yyyy-MM-dd" onChange={d => onUpdateEndDate(bonus, d)} onFocus={e => e.target.blur()} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => onModifyBonus(bonus)}>{text.saveChangesToBonus}</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        allbonuses: state.allbonuses,
        bonus: state.bonus || {},
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onUpdateBonus: bonus => dispatch(UPDATE_BONUS(bonus)),
        onUpdateEnabled: (bonus, e) => dispatch(UPDATE_BONUS({ ...bonus, enabled: e.target.checked })),
        onUpdateIconurl: (bonus, e) => dispatch(UPDATE_BONUS({ ...bonus, iconurl: e.target.value })),
        onUpdateTitle: (bonus, e) => dispatch(UPDATE_BONUS({ ...bonus, title: e.target.value })),
        onUpdateDescription: (bonus, e) => dispatch(UPDATE_BONUS({ ...bonus, description: e.target.value })),
        onUpdateBonusFactor: (bonus, e) => dispatch(UPDATE_BONUS({ ...bonus, bonusFactor: e.target.value })),
        onUpdateStartDate: (bonus, startDate) => dispatch(UPDATE_BONUS({ ...bonus, startDate })),
        onUpdateEndDate: (bonus, endDate) => dispatch(UPDATE_BONUS({ ...bonus, endDate })),
        onModifyBonus: bonus => {
            if (parseInt(bonus.bonusId) !== emptyBonus.bonusId) {
                dispatch(MODIFY_BONUS(bonus));
                dispatch(UPDATE_BONUS({ ...emptyBonus }));
            }
        },
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminBonusesModify);
