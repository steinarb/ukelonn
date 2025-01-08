import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useGetPaymenttypesQuery } from '../api';
import { SELECTED_PAYMENT_TYPE } from '../actiontypes';

function Paymenttypes(props) {
    const { id, className } = props;
    const paymentTypeId = useSelector(state => state.transactionTypeId);
    const { data: paymenttypes = [] } = useGetPaymenttypesQuery();
    const dispatch = useDispatch();
    const onPaymentTypeSelected = e => dispatch(SELECTED_PAYMENT_TYPE(paymenttypes.find(t => t.id === parseInt(e.target.value)) || { id: -1 }));

    return (
        <select id={id} className={className} onChange={onPaymentTypeSelected} value={paymentTypeId}>
            <option key="-1" value="-1" />
            {paymenttypes.map((val) => <option key={val.id} value={val.id}>{val.transactionTypeName}</option>)}
        </select>
    );
}

export default Paymenttypes;
