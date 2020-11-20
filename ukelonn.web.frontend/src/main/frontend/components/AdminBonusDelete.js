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
            <Link to="/ukelonn/admin/bonuses">
                &lt;-
                &nbsp;
                Administer bonuser
            </Link>
            <header>
                <div>
                    <h1>Slett bonuser</h1>
                </div>
            </header>

            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="bonus">Velg bonus</label>
                        <div>
                            <select id="bonus" value={bonusId} onChange={e => onUpdateBonus(bonuses.find(b => b.bonusId === parseInt(e.target.value)))}>
                                {bonuses.map(b => <option key={b.bonusId} value={b.bonusId}>{b.title}</option>)}
                            </select>
                        </div>
                    </div>
                    <div>
                        <label htmlFor="title">Tittel</label>
                        <div>
                            <input readOnly id="title" type="text" value={title} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="description">Beskrivelse</label>
                        <div>
                            <input readOnly id="description" type="text" value={description} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => onDeleteBonus(bonus)}>Slett valgt bonus</button>
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
}

function mapStateToProps(state) {
    return {
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
