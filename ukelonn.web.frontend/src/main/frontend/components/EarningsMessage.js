import React from 'react';
import { connect } from 'react-redux';

function EarningsMessage(props) {
    const { text, earningsSumOverYear, earningsSumOverMonth } = props;

    if (!(earningsSumOverYear.length || earningsSumOverMonth.length)) {
        return '';
    }

    const yearMessage = messageForEarningsCurrentAndPreviousYear(earningsSumOverYear, text);
    const monthMessage = messageForEarningsCurrentMonthAndPreviousMonth(earningsSumOverMonth, text);
    return (<div className="alert alert-info" role="alert">{yearMessage}{monthMessage}</div>);
}

function messageForEarningsCurrentAndPreviousYear(earningsSumOverYear, text) {
    let message = '';
    if (earningsSumOverYear.length) {
        const totalEarningsThisYear = earningsSumOverYear[earningsSumOverYear.length - 1].sum;
        message = text.totalAmountEarnedThisYear + ': ' + totalEarningsThisYear;
        if (earningsSumOverYear.length > 1) {
            const previousYear = earningsSumOverYear[earningsSumOverYear.length - 2];
            message = message.concat(' (', text.against, ' ', previousYear.sum, ' ', text.earnedForAllOf, ' ', previousYear.year, ')');
        }
    }

    return (<div>{message}</div>);
}

function messageForEarningsCurrentMonthAndPreviousMonth(earningsSumOverMonth, text) {
    let message = '';
    if (earningsSumOverMonth.length) {
        const totalEarningsThisMonth = earningsSumOverMonth[earningsSumOverMonth.length - 1].sum;
        message = message.concat(text.totalAmountEarnedThisMonth + ': ', totalEarningsThisMonth);
        if (earningsSumOverMonth.length > 1) {
            // The previous month may not actually be the previous month if the kids have been lazy
            // but we do care about that level of detail here (or at least: I don't...)
            const previousMonth = earningsSumOverMonth[earningsSumOverMonth.length - 2];
            message = message.concat(' (', text.against, ' ', previousMonth.sum, ' ',text.earnedForAllOfThePreviousMonth, ')');
        }
    }

    return (<div>{message}</div>);
}


function mapStateToProps(state) {
    const earningsSumOverYear = state.earningsSumOverYear || [];
    const earningsSumOverMonth = state.earningsSumOverMonth || [];
    return {
        text: state.displayTexts,
        earningsSumOverYear,
        earningsSumOverMonth,
    };
}


export default connect(mapStateToProps)(EarningsMessage);
