import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useGetAllbonusesQuery } from '../api';
import { selectBonus } from '../reducers/bonusSlice';

export default function Bonuses() {
    const { data: allbonuses = [] } = useGetAllbonusesQuery();
    const bonusId = useSelector(state => state.bonusId);
    const dispatch = useDispatch();
    const onBonusSelected = e => dispatch(selectBonus(findSelectedBonus(e, allbonuses)));

    return (
        <select id="bonus" className="form-control" value={bonusId} onChange={onBonusSelected}>
            <option key="-1" value="-1" />
            {allbonuses.map(b => <option key={b.bonusId} value={b.bonusId}>{b.title}</option>)}
        </select>
    );
}


function findSelectedBonus(e, allbonuses) {
    const bonusId = parseInt(e.target.value);
    return allbonuses.find(b => b.bonusId === bonusId) || { bonusId };
}
