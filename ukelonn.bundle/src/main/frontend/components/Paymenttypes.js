import React from 'react';

const Paymenttypes = ({id, className, paymenttypes, paymenttypesMap, value, account, paymenttype, onPaymenttypeFieldChange }) => (
    <select id={id} className={className} onChange={(event) => onPaymenttypeFieldChange(event.target.value, paymenttypesMap, account)} value={value}>
        {paymenttypes.map((val) => <option key={val.id}>{val.transactionTypeName}</option>)}
    </select>
);

export default Paymenttypes;
