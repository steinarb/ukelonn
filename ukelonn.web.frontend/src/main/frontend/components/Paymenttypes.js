import React from 'react';

function Paymenttypes(props) {
    const {id, className, value, paymenttypes, account, onPaymenttypeFieldChange } = props;
    return (
        <select id={id} className={className} onChange={(event) => onPaymenttypeFieldChange(event.target.value, paymenttypes, account)} value={value}>
            {paymenttypes.map((val) => <option key={val.id} value={val.id}>{val.transactionTypeName}</option>)}
        </select>
    );
}

export default Paymenttypes;
