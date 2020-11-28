import React from 'react';
import { connect } from 'react-redux';
import moment from 'moment';

function BonusBanner(props) {
    const { activebonuses } = props;
    if (!activebonuses.length) {
        return null;
    }

    return (
        <div className="container">
            {activebonuses.map(renderBonus)}
        </div>
    );
}

function mapStateToProps(state) {
    const activebonuses = state.activebonuses || [];
    return {
        activebonuses,
    };
}

export default connect(mapStateToProps)(BonusBanner);

function renderBonus(bonus, idx) {
    const key = 'bonus' + idx.toString();
    const daysRemaining = moment(bonus.endDate).diff(moment(), 'days');
    if (!bonus.iconurl) {
        return (
            <div key={key} className="alert alert-info container" role="alert">
                <div className="row">{bonus.title} aktiv! ({daysRemaining} dager igjen)</div>
                <div className="row">{bonus.description}</div>
            </div>
        );
    }

    return (
        <div key={key} className="alert alert-info row" role="alert">
            <div className="col col-md-auto"><img src={bonus.iconurl}/></div>
            <div className="col">
                <div className="row">{bonus.title} aktiv! ({daysRemaining} dager igjen)</div>
                <div className="row">{bonus.description}</div>
            </div>
        </div>
    );
}
