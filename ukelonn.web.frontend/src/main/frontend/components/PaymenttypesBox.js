import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { SELECT_PAYMENT_TYPE_FOR_EDIT } from '../actiontypes';

export default function PaymenttypesBox(props) {
    const {id, className } = props;
    const paymenttypes = useSelector(state => state.paymenttypes);
    const transactionTypeId = useSelector(state => state.transactionTypeId);
    const dispatch = useDispatch();

    return (
        <select multiple="true" size="10" id={id} className={className} onChange={e => dispatch(SELECT_PAYMENT_TYPE_FOR_EDIT(parseInt(e.target.value)))} value={transactionTypeId}>
            <option key="-1" value="-1" />
            {paymenttypes.map(val => <option key={val.id} value={val.id}>{val.transactionTypeName}</option>)}
        </select>
    );
}
