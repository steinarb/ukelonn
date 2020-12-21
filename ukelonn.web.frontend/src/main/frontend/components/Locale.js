import React from 'react';
import { connect } from 'react-redux';
import {
    UPDATE_LOCALE,
} from '../actiontypes';

function Locale(props) {
    const { className, locale, availableLocales, onUpdateLocale } = props;

    return (
        <select className={className} onChange={(event) => onUpdateLocale(event.target.value)} value={locale}>
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

function mapDispatchToProps(dispatch) {
    return {
        onUpdateLocale: locale => dispatch(UPDATE_LOCALE(locale)),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Locale);
