import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { LOGOUT_REQUEST } from '../actiontypes';

export default function Logout() {
    const text = useSelector(state => state.displayTexts);
    const dispatch = useDispatch();

    return (<button onClick={() => dispatch(LOGOUT_REQUEST())}>{text.logout}</button>);
}
