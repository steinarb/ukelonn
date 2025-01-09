import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useGetJobtypesQuery } from '../api';
import { SELECTED_JOB_TYPE } from '../actiontypes';

export default function Jobtypes(props) {
    const { id, className } = props;
    const transactionTypeId = useSelector(state => state.transactionTypeId);
    const { data: jobtypes = [] } = useGetJobtypesQuery()
    const dispatch = useDispatch();
    const onJobTypeSelected = e => dispatch(SELECTED_JOB_TYPE(jobtypes.find(t => t.id === parseInt(e.target.value)) || { id: -1 }));

    return (
        <select id={id} className={className} onChange={onJobTypeSelected} value={transactionTypeId}>
            <option key="-1" value="-1" />
            {jobtypes.map((val) => <option key={val.id} value={val.id}>{val.transactionTypeName}</option>)}
        </select>
    );
}
