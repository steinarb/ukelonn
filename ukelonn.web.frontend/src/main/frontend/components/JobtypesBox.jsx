import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { selectJobType } from '../reducers/transactionTypeSlice';
import { useGetJobtypesQuery } from '../api';

export default function JobtypesBox(props) {
    const { id, className } = props;
    const transactionTypeId = useSelector(state => state.transactionTypeId);
    const { data: jobtypes = [] } = useGetJobtypesQuery()
    const dispatch = useDispatch();
    const onJobTypeSelected = (e) => { dispatch(selectJobType(jobtypes.find(t => t.id === parseInt(e.target.value)) || { id: -1 })) }

    return (
        <select multiple="true" size="10" id={id} className={className} onChange={onJobTypeSelected} value={transactionTypeId}>
            <option key="-1" value="-1" />
            {jobtypes.map((val) => <option key={val.id} value={val.id}>{val.transactionTypeName}</option>)}
        </select>
    );
}
