import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { UPDATE_LOCALE } from '../actiontypes';

export default function Locale(props) {
    const { className } = props;
    const locale = useSelector(state => state.locale);
    const availableLocales = useSelector(state => state.availableLocales);
    const dispatch = useDispatch();

    return (
        <select className={className} onChange={e => dispatch(UPDATE_LOCALE(e.target.value))} value={locale}>
            {availableLocales.map((l) => <option key={'locale_' + l.code} value={l.code}>{l.displayLanguage}</option>)}
        </select>
    );
}
