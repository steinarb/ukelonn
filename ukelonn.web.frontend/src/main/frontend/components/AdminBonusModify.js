import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
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
        onSelectBonus,
        onUpdateEnabled,
        onUpdateIconurl,
        onUpdateTitle,
        onUpdateDescription,
        onUpdateBonusFactor,
        onUpdateStartDate,
        onUpdateEndDate,
        onSaveModifyBonusButtonClicked,
        onLogout,
    } = props;

    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/ukelonn/admin/bonuses">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateBonuses}
                </Link>
                <h1>{text.modifyBonuses}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="bonus" className="col-form-label col-5">{text.chooseBonus}</label>
                        <div className="col-7">
                            <select id="bonus" value={bonusId} onChange={onSelectBonus}>
                                <option key="-1" value="-1" />
                                {allbonuses.map(b => <option key={b.bonusId} value={b.bonusId}>{b.title}</option>)}
                            </select>
                        </div>
                    </div>
                    <div className="row">
                        <div className="col">
                            <div className="form-check">
                            <input id="enabled" type="checkbox" checked={bonusEnabled} onChange={onUpdateEnabled} />
                                <label htmlFor="enabled" className="form-check-label">{text.activated}</label>
                            </div>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="iconurl" className="col-form-label col-5">{text.iconURL}</label>
                        <div className="col-7">
                            <input id="iconurl" type="text" value={bonusIconurl} onChange={onUpdateIconurl} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="title" className="col-form-label col-5">{text.title}</label>
                        <div className="col-7">
                            <input id="title" type="text" value={bonusTitle} onChange={onUpdateTitle} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="description" className="col-form-label col-5">{text.description}</label>
                        <div className="col-7">
                            <input id="description" type="text" value={bonusDescription} onChange={onUpdateDescription} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="bonusfactor" className="col-form-label col-5">{text.bonusFactor}</label>
                        <div className="col-7">
                            <input id="bonusfactor" type="text" pattern="[0-9]?[.]?[0-9]?[0-9]?" value={bonusFactor} onChange={onUpdateBonusFactor} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="startdate" className="col-form-label col-5">{text.startDate}</label>
                        <div className="col-7">
                            <DatePicker selected={new Date(bonusStartDate)} dateFormat="yyyy-MM-dd" onChange={onUpdateStartDate} onFocus={e => e.target.blur()} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="enddate" className="col-form-label col-5">{text.endDate}</label>
                        <div className="col-7">
                            <DatePicker selected={new Date(bonusEndDate)} dateFormat="yyyy-MM-dd" onChange={onUpdateEndDate} onFocus={e => e.target.blur()} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button onClick={onSaveModifyBonusButtonClicked}>{text.saveChangesToBonus}</button>
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

function mapDispatchToProps(dispatch) {
    return {
        onSelectBonus: e => dispatch(SELECT_BONUS(parseInt(e.target.value))),
        onUpdateEnabled: e => dispatch(MODIFY_BONUS_ENABLED(e.target.checked)),
        onUpdateIconurl: e => dispatch(MODIFY_BONUS_ICONURL(e.target.value)),
        onUpdateTitle: e => dispatch(MODIFY_BONUS_TITLE(e.target.value)),
        onUpdateDescription: e => dispatch(MODIFY_BONUS_DESCRIPTION(e.target.value)),
        onUpdateBonusFactor: e => dispatch(MODIFY_BONUS_FACTOR(e.target.value)),
        onUpdateStartDate: d => dispatch(MODIFY_BONUS_START_DATE(d)),
        onUpdateEndDate: d => dispatch(MODIFY_BONUS_END_DATE(d)),
        onSaveModifyBonusButtonClicked: () => dispatch(SAVE_BONUS_CHANGES_BUTTON_CLICKED()),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminBonusesModify);
