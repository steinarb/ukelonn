import React from 'react';

function PaymenttypesBox(props) {
    const {id, className, paymenttypes, value, onPaymenttypeFieldChange } = props;
    return (
        <select multiselect="true" size="10" id={id} className={className} onChange={(event) => onPaymenttypeFieldChange(event.target.value, paymenttypes)} value={value}>
          {paymenttypes.map((val) => <option key={val.id} value={val.id}>{val.transactionTypeName}</option>)}
        </select>
    );
}

export default PaymenttypesBox;
