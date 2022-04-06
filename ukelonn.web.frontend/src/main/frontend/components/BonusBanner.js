import React from 'react';
import { connect } from 'react-redux';

function BonusBanner(props) {
    const { text, activebonuses } = props;
    if (!activebonuses.length) {
        return null;
    }

    return (
        <div>
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
    return (
        <div key={key}>
            <div><BonusIcon bonus={bonus}/>{bonus.title} {text.active}! ({daysRemaining} {text.daysRemaining})</div>
            <div>{bonus.description}</div>
        </div>
    );
}

function BonusIcon(props) {
    const { bonus } = props;
    if (!bonus.iconurl) {
        return null;
    }

    return (<img src={bonus.iconurl}/>);
}

function dateDayDiff(d1, d2) {
    const difference = d1.getTime() - d2.getTime();
    return Math.ceil(difference / (1000 * 36000 * 2));
}
