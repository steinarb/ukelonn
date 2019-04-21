import React from 'react';

const Accounts = ({id, className, accounts, accountsMap, account, paymenttype, onAccountsFieldChange }) => (
    <select id={id} className={className} onChange={(event) => onAccountsFieldChange(event.target.value, accountsMap, paymenttype)} value={account.fullName}>
        {accounts.map((val) => <option key={'account_' + val.accountId}>{val.fullName}</option>)}
    </select>
);

export default Accounts;
