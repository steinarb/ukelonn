import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { selectJobType } from '../reducers/transactionTypeSlice';
import { useGetPaymenttypesQuery } from '../api';

export default function PaymenttypesBox(props) {
    const {id, className } = props;
    const { data: paymenttypes = [] } = useGetPaymenttypesQuery();
    const transactionTypeId = useSelector(state => state.transactionTypeId);
    const dispatch = useDispatch();
    const onPaymentTypeSelected = e => dispatch(selectJobType(paymenttypes.find(t => t.id === parseInt(e.target.value)) || { id: -1 }));

    return (
        <select multiple="true" size="10" id={id} className={className} onChange={onPaymentTypeSelected} value={transactionTypeId}>
            <option key="-1" value="-1" />
            {paymenttypes.map(val => <option key={val.id} value={val.id}>{val.transactionTypeName}</option>)}
        </select>
    );
}
