import React from 'react';
import { useSelector } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    useGetActivebonusesQuery,
} from '../api';

export default function BonusBanner() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const { data: activebonuses = [] } = useGetActivebonusesQuery();
    if (!activebonuses.length) {
        return null;
    }

    return (
        <div className="container">
            {activebonuses.map((b, idx) => renderBonus(b, idx, text))}
        </div>
    );
}

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
