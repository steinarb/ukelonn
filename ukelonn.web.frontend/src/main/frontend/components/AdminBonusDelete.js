import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    SELECT_BONUS,
    DELETE_SELECTED_BONUS_BUTTON_CLICKED,
} from '../actiontypes';
import Locale from './Locale';

function AdminBonusesDelete(props) {
    const {
        text,
        allbonuses,
        bonusId,
        bonusTitle,
        bonusDescription,
        onSelectBonus,
        onDeleteBonus,
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
                <h1>{text.deleteBonuses}</h1>
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
                    <div className="form-group row">
                        <label htmlFor="title" className="col-form-label col-5">{text.title}</label>
                        <div className="col-7">
                            <input readOnly id="title" type="text" value={bonusTitle} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="description" className="col-form-label col-5">{text.description}</label>
                        <div className="col-7">
                            <input readOnly id="description" type="text" value={bonusDescription} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button onClick={() => onDeleteBonus()}>{text.deleteSelectedBonus}</button>
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
        bonusId: state.bonusId,
        bonusTitle: state.bonusTitle,
        bonusDescription: state.bonusDescription,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onSelectBonus: e => dispatch(SELECT_BONUS(parseInt(e.target.value))),
        onDeleteBonus: () => dispatch(DELETE_SELECTED_BONUS_BUTTON_CLICKED()),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminBonusesDelete);
