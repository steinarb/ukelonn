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
            <Link to="/ukelonn/admin/bonuses">
                &lt;-
                &nbsp;
                Administer bonuser
            </Link>
            <header>
                <div>
                    <h1>Lag ny bonus</h1>
                </div>
            </header>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="enabled">Aktivert</label>
                        <div>
                            <input id="enabled" type="checkbox" checked={enabled} onChange={e => onUpdateEnabled(bonus, e)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="iconurl">Ikon-URL</label>
                        <div>
                            <input id="iconurl" type="text" value={iconurl} onChange={e => onUpdateIconurl(bonus, e)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="title">Tittel</label>
                        <div>
                            <input id="title" type="text" value={title} onChange={e => onUpdateTitle(bonus, e)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="description">Beskrivelse</label>
                        <div>
                            <input id="description" type="text" value={description} onChange={e => onUpdateDescription(bonus, e)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="bonusfactor">Bonusfaktor</label>
                        <div>
                            <input id="bonusfactor" type="text" pattern="[0-9]?[.]?[0-9]?[0-9]?" value={bonusFactor} onChange={e => onUpdateBonusFactor(bonus, e)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="startdate">Startdato</label>
                        <div>
                            <DatePicker selected={startDate} dateFormat="YYYY-MM-DD" onChange={d => onUpdateStartDate(bonus, d)} readOnly={true} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="enddate">Sluttdato</label>
                        <div>
                            <DatePicker selected={endDate} dateFormat="YYYY-MM-DD" onChange={d => onUpdateEndDate(bonus, d)} readOnly={true} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => onCreateBonus(bonus)}>Lag ny bonus</button>
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
