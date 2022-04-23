import React from 'react';
import { connect, useDispatch } from 'react-redux';
import {
    UPDATE_LOCALE,
} from '../actiontypes';

function Locale(props) {
    const { className, locale, availableLocales } = props;
    const dispatch = useDispatch();

    return (
        <select className={className} onChange={e => dispatch(UPDATE_LOCALE(e.target.value))} value={locale}>
            {availableLocales.map((l) => <option key={'locale_' + l.code} value={l.code}>{l.displayLanguage}</option>)}
        </select>
    );
}

function mapStateToProps(state) {
    const { locale, availableLocales } = state;
    return {
        locale,
        availableLocales,
    };
}

export default connect(mapStateToProps)(Locale);
