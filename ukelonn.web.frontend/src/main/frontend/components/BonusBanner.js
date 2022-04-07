import React from 'react';
import { connect } from 'react-redux';

function BonusBanner(props) {
    const { text, activebonuses } = props;
    if (!activebonuses.length) {
        return null;
    }

    return (
        <div className="container">
            {activebonuses.map((b, idx) => renderBonus(b, idx, text))}
        </div>
    );
}

function mapStateToProps(state) {
    const activebonuses = state.activebonuses || [];
    return {
        text: state.displayTexts,
        activebonuses,
    };
}

export default connect(mapStateToProps)(BonusBanner);

function renderBonus(bonus, idx, text) {
    const key = 'bonus' + idx.toString();
    const daysRemaining = dateDayDiff(new Date(bonus.endDate), new Date());
    if (!bonus.iconurl) {
        return (
            <div key={key} className="alert alert-info container" role="alert">
                <div className="row">{bonus.title} {text.active}! ({daysRemaining} {text.daysRemaining})</div>
                <div className="row">{bonus.description}</div>
            </div>
        );
    }

    return (
        <div key={key} className="alert alert-info row" role="alert">
            <div className="col col-md-auto"><img src={bonus.iconurl}/></div>
            <div className="col">
                <div className="row">{bonus.title} {text.active}! ({daysRemaining} {text.daysRemaining})</div>
                <div className="row">{bonus.description}</div>
            </div>
        </div>
    );
}

function dateDayDiff(d1, d2) {
    const difference = d1.getTime() - d2.getTime();
    return Math.ceil(difference / (1000 * 36000 * 2));
}
