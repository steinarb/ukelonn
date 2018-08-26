import React from 'react';

const Amount = ({id, className, payment, onAmountFieldChange }) => (
    <input id={id} className={className} type="text" value={payment.transactionAmount} onChange={(event) => onAmountFieldChange(event.target.value, payment)} />
);

export default Amount;
