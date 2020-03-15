import React from 'react';

function Amount(props) {
    const {id, className, payment, onAmountFieldChange } = props;
    return (
        <input id={id} className={className} className="mdl-textfield__input" type="text" value={payment.transactionAmount} onChange={(event) => onAmountFieldChange(event.target.value)} />
    );
}

export default Amount;
