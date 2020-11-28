import React from 'react';
import { connect } from 'react-redux';
import moment from 'moment';

function BonusBanner(props) {
    const { activebonuses } = props;
    if (!activebonuses.length) {
        return null;
    }

    return (
        <div>
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
    return (
        <div key={key}>
            <div><BonusIcon bonus={bonus}/>{bonus.title} aktiv! ({daysRemaining} dager igjen)</div>
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
