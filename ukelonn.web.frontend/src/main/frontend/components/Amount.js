import React from 'react';

function Amount(props) {
    const {id, className, payment, onAmountFieldChange } = props;
    return (
        <input id={id} className={className} type="text" value={payment.transactionAmount} onChange={(event) => onAmountFieldChange(event.target.value)} />
    );
}

export default Amount;
