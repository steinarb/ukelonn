import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { SELECT_JOB_TYPE } from '../actiontypes';

function JobtypesBox(props) {
    const { id, className } = props;
    const transactionTypeId = useSelector(state => state.transactionTypeId);
    const jobtypes = useSelector(state => state.jobtypes);
    const dispatch = useDispatch();

    return (
        <select multiselect="true" size="10" id={id} className={className} onChange={e => dispatch(SELECT_JOB_TYPE(parseInt(e.target.value)))} value={transactionTypeId}>
          {jobtypes.map((val) => <option key={val.id} value={val.id}>{val.transactionTypeName}</option>)}
        </select>
    );
}

export default JobtypesBox;
