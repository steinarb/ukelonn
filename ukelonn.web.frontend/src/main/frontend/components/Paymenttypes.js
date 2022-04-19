import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { SELECT_PAYMENT_TYPE } from '../actiontypes';

function Paymenttypes(props) {
    const { id, className } = props;
    const paymentTypeId = useSelector(state => state.paymentTypeId);
    const paymenttypes = useSelector(state => state.paymenttypes);
    const dispatch = useDispatch();

    return (
        <select id={id} className={className} onChange={e => dispatch(SELECT_PAYMENT_TYPE(parseInt(e.target.value)))} value={paymentTypeId}>
            <option key="-1" value="-1" />
            {paymenttypes.map((val) => <option key={val.id} value={val.id}>{val.transactionTypeName}</option>)}
        </select>
    );
}

export default Paymenttypes;
