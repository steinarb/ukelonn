import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { SELECT_ACCOUNT } from '../actiontypes';

function Accounts(props) {
    const { id, className } = props;
    const accounts = useSelector(state => state.accounts);
    const accountId = useSelector(state => state.accountId);
    const dispatch = useDispatch();

    return (
        <select id={id} className={className} onChange={e => dispatch(SELECT_ACCOUNT(parseInt(e.target.value)))} value={accountId}>
            <option key="account_-1" value="-1" />
            {accounts.map((val) => <option key={'account_' + val.accountId} value={val.accountId}>{val.fullName}</option>)}
        </select>
    );
}

export default Accounts;
