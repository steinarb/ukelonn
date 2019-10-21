import React from 'react';

function Accounts(props) {
    const {id, className, accounts, accountsMap, account, paymenttype, onAccountsFieldChange } = props;
    return (
        <select id={id} className={className} onChange={(event) => onAccountsFieldChange(event.target.value, accountsMap, paymenttype)} value={account.fullName}>
          {accounts.map((val) => <option key={'account_' + val.accountId}>{val.fullName}</option>)}
        </select>
    );
}

export default Accounts;
