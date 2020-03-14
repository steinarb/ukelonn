import React from 'react';

function JobtypesBox(props) {
    const {id, className, value, jobtypes, onJobtypeFieldChange } = props;
    return (
        <select multiselect="true" size="10" id={id} className={className} onChange={(event) => onJobtypeFieldChange(event.target.value, jobtypes)} value={value}>
          {jobtypes.map((val) => <option key={val.id} value={val.id}>{val.transactionTypeName}</option>)}
        </select>
    );
}

export default JobtypesBox;
