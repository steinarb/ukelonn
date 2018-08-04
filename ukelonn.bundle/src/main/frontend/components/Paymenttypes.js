import React from 'react';

const Paymenttypes = ({id, className, paymenttypes, paymenttypesMap, account, paymenttype, onPaymenttypeFieldChange }) => (
    <select id={id} className={className} onChange={(event) => onPaymenttypeFieldChange(event.target.value, paymenttypesMap, account)} value={paymenttype.transactionTypeName}>
        {paymenttypes.map((val) => <option key={val.id}>{val.transactionTypeName}</option>)}
    </select>
);

export default Paymenttypes;
