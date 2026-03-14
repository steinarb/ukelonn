import React from 'react';
import {
    useGetLoginQuery,
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    useGetSumoveryearQuery,
    useGetSumovermonthQuery,
} from '../api';
import { useSelector } from 'react-redux';

export default function EarningsMessage() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const { data: loginStatus = {}, isSuccess : loginIsSuccess } = useGetLoginQuery();
    const { data: earningsSumOverYear = [] } = useGetSumoveryearQuery(loginStatus.username, { skip: !loginIsSuccess });
    const { data: earningsSumOverMonth = [] } = useGetSumovermonthQuery(loginStatus.username, { skip: !loginIsSuccess });

    if (!(earningsSumOverYear.length || earningsSumOverMonth.length)) {
        return '';
    }

    const yearMessage = messageForEarningsCurrentAndPreviousYear(earningsSumOverYear, text);
    const monthMessage = messageForEarningsCurrentMonthAndPreviousMonth(earningsSumOverMonth, text);
    return (<div>{yearMessage}{monthMessage}</div>);
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
