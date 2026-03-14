import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useGetJobtypesQuery } from '../api';
import { selectJobType } from '../reducers/transactionSlice';

export default function Jobtypes(props) {
    const { id, className } = props;
    const transaction = useSelector(state => state.transaction);
    const { data: jobtypes = [] } = useGetJobtypesQuery()
    const dispatch = useDispatch();
    const onJobTypeSelected = e => dispatch(selectJobType(jobtypes.find(t => t.id === parseInt(e.target.value)) || { id: -1 }));

    return (
        <select id={id} className={className} onChange={onJobTypeSelected} value={transaction.transactionType.id}>
            <option key="-1" value="-1" />
            {jobtypes.map((val) => <option key={val.id} value={val.id}>{val.transactionTypeName}</option>)}
        </select>
    );
}
