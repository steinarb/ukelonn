import React from 'react';

function Accounts(props) {
    const { id, className, value, accounts, onAccountsFieldChange } = props;
    return (
        <select id={id} className={className} onChange={(event) => onAccountsFieldChange(event.target.value, accounts)} value={value}>
            {accounts.map((val) => <option key={'account_' + val.accountId} value={val.accountId}>{val.fullName}</option>)}
        </select>
    );
}

export default Accounts;
