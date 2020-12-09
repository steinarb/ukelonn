import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    UPDATE_BONUS,
    CREATE_BONUS,
} from '../actiontypes';
import { emptyBonus } from '../constants';

function AdminBonusCreate(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    const {
        bonuses,
        bonus,
        onUpdateEnabled,
        onUpdateIconurl,
        onUpdateTitle,
        onUpdateDescription,
        onUpdateBonusFactor,
        onUpdateStartDate,
        onUpdateEndDate,
        onCreateBonus,
        onLogout,
    } = props;
    const enabled = bonus.enabled;
    const iconurl = bonus.iconurl || '';
    const title = bonus.title || '';
    const description = bonus.description || '';
    const bonusFactor = bonus.bonusFactor || 0;
    const startDate = bonus.startDate;
    const endDate = bonus.endDate;

    return (
        <div>
            <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/admin/bonuses">
                <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                &nbsp;
                Administer bonuser
            </Link>
            <header>
                <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                    <h1>Lag ny bonus</h1>
                </div>
            </header>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div clasName="row">
                        <div className="col">
                            <div className="form-check">
                                <div className="form-check">
                                    <input id="enabled" className="form-check-input" type="checkbox" checked={enabled} onChange={e => onUpdateEnabled(bonus, e)} />
                                    <label htmlFor="enabled" className="form-check-label">Aktivert</label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="iconurl" className="col-form-label col-5">Ikon-URL</label>
                        <div className="col-7">
                            <input id="iconurl" className="form-control" type="text" value={iconurl} onChange={e => onUpdateIconurl(bonus, e)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="title" className="col-form-label col-5">Tittel</label>
                        <div className="col-7">
                            <input id="title" className="form-control" type="text" value={title} onChange={e => onUpdateTitle(bonus, e)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="description" className="col-form-label col-5">Beskrivelse</label>
                        <div className="col-7">
                            <input id="description" className="form-control" type="text" value={description} onChange={e => onUpdateDescription(bonus, e)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="bonusfactor" className="col-form-label col-5">Bonusfaktor</label>
                        <div className="col-7">
                            <input id="bonusfactor" className="form-control" type="text" pattern="[0-9]?[.]?[0-9]?[0-9]?" value={bonusFactor} onChange={e => onUpdateBonusFactor(bonus, e)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="startdate" className="col-form-label col-5">Startdato</label>
                        <div className="col-7">
                            <DatePicker selected={startDate} dateFormat="YYYY-MM-DD" onChange={d => onUpdateStartDate(bonus, d)} readOnly={true} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="enddate" className="col-form-label col-5">Sluttdato</label>
                        <div className="col-7">
                            <DatePicker selected={endDate} dateFormat="YYYY-MM-DD" onChange={d => onUpdateEndDate(bonus, d)} readOnly={true} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => onCreateBonus(bonus)}>Lag ny bonus</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>Logout</button>
            <br/>
            <a href="../../../..">Tilbake til topp</a>
        </div>
    );
};

function mapStateToProps(state) {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        allbonuses: state.allbonuses,
        bonus: state.bonus,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onUpdateEnabled: (bonus, e) => dispatch(UPDATE_BONUS({ ...bonus, enabled: e.target.checked })),
        onUpdateIconurl: (bonus, e) => dispatch(UPDATE_BONUS({ ...bonus, iconurl: e.target.value })),
        onUpdateTitle: (bonus, e) => dispatch(UPDATE_BONUS({ ...bonus, title: e.target.value })),
        onUpdateDescription: (bonus, e) => dispatch(UPDATE_BONUS({ ...bonus, description: e.target.value })),
        onUpdateBonusFactor: (bonus, e) => dispatch(UPDATE_BONUS({ ...bonus, bonusFactor: e.target.value })),
        onUpdateStartDate: (bonus, startDate) => dispatch(UPDATE_BONUS({ ...bonus, startDate })),
        onUpdateEndDate: (bonus, endDate) => dispatch(UPDATE_BONUS({ ...bonus, endDate })),
        onCreateBonus: bonus => {
            dispatch(CREATE_BONUS(bonus));
            dispatch(UPDATE_BONUS({ ...emptyBonus }));
        },
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminBonusCreate);
