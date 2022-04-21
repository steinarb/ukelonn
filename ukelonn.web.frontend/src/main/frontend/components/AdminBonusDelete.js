import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
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

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/bonuses">
                    &lt;-
                    &nbsp;
                    {text.administrateBonuses}
                </Link>
                <h1>{text.deleteBonuses}</h1>
                <Locale />
            </nav>

            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="bonus">{text.chooseBonus}</label>
                        <div>
                            <select id="bonus" value={bonusId} onChange={onSelectBonus}>
                                <option key="-1" value="-1" />
                                {allbonuses.map(b => <option key={b.bonusId} value={b.bonusId}>{b.title}</option>)}
                            </select>
                        </div>
                    </div>
                    <div>
                        <label htmlFor="title">{text.title}</label>
                        <div>
                            <input readOnly id="title" type="text" value={bonusTitle} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="description">{text.description}</label>
                        <div>
                            <input readOnly id="description" type="text" value={bonusDescription} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
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
