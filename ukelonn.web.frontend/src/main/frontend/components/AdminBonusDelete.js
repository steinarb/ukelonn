import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    UPDATE_BONUS,
    DELETE_BONUS,
} from '../actiontypes';
import Locale from './Locale';
import { emptyBonus } from '../constants';

function reloadJobListWhenAccountHasChanged(oldAccount, newAccount, loadBonuses) {
    if (oldAccount !== newAccount) {
        loadBonuses(newAccount);
    }
}

function AdminBonusesDelete(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let {
        text,
        allbonuses,
        bonus,
        onUpdateBonus,
        onDeleteBonus,
        onLogout,
    } = props;
    const bonuses = [emptyBonus].concat(allbonuses);
    const bonusId = bonus.bonusId;
    const title = bonus.title || '';
    const description = bonus.description || '';

    return (
        <div>
            <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/admin/bonuses">
                <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                &nbsp;
                {text.administrateBonuses}
            </Link>
            <header>
                <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                    <h1>{text.deleteBonuses}</h1>
                </div>
                <div>
                    <Locale />
                </div>
            </header>

            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="bonus" className="col-form-label col-5">{text.chooseBonus}</label>
                        <div className="col-7">
                            <select id="bonus" className="form-control" value={bonusId} onChange={e => onUpdateBonus(bonuses.find(b => b.bonusId === parseInt(e.target.value)))}>
                                {bonuses.map(b => <option key={b.bonusId} value={b.bonusId}>{b.title}</option>)}
                            </select>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="title" className="col-form-label col-5">{text.title}</label>
                        <div className="col-7">
                            <input readOnly id="title" className="form-control" type="text" value={title} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="description" className="col-form-label col-5">{text.description}</label>
                        <div className="col-7">
                            <input readOnly id="description" className="form-control" type="text" value={description} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => onDeleteBonus(bonus)}>{text.deleteSelectedBonus}</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
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
        onDeleteBonus: bonus => {
            if (parseInt(bonus.bonusId) !== emptyBonus.bonusId) {
                dispatch(DELETE_BONUS(bonus));
                dispatch(UPDATE_BONUS({ ...emptyBonus }));
            }
        },
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminBonusesDelete);
