import React, { Component } from 'react';
import { connect } from 'react-redux';

class EarningsMessage extends Component {

    render() {
        const { earningsSumOverYear, earningsSumOverMonth } = this.props;

        if (!(earningsSumOverYear.length || earningsSumOverMonth.length)) {
            return '';
        }

        const yearMessage = messageForEarningsCurrentAndPreviousYear(earningsSumOverYear);
        const monthMessage = messageForEarningsCurrentMonthAndPreviousMonth(earningsSumOverMonth);
        return (<div className="alert alert-info" role="alert">{yearMessage}{monthMessage}</div>);
    }
}

function messageForEarningsCurrentAndPreviousYear(earningsSumOverYear) {
    let message = '';
    if (earningsSumOverYear.length) {
        const totalEarningsThisYear = earningsSumOverYear[earningsSumOverYear.length - 1].sum;
        message = 'Totalt beløp tjent i år: ' + totalEarningsThisYear;
        if (earningsSumOverYear.length > 1) {
            const previousYear = earningsSumOverYear[earningsSumOverYear.length - 2];
            message = message.concat(' (mot ', previousYear.sum, ' tjent i hele ', previousYear.year, ')');
        }
    }

    return (<div>{message}</div>);
}

function messageForEarningsCurrentMonthAndPreviousMonth(earningsSumOverMonth) {
    let message = '';
    if (earningsSumOverMonth.length) {
        const totalEarningsThisMonth = earningsSumOverMonth[earningsSumOverMonth.length - 1].sum;
        message = message.concat('Totalt beløp tjent denne måneden: ', totalEarningsThisMonth);
        if (earningsSumOverMonth.length > 1) {
            // The previous month may not actually be the previous month if the kids have been lazy
            // but we do care about that level of detail here (or at least: I don't...)
            const previousMonth = earningsSumOverMonth[earningsSumOverMonth.length - 2];
            message = message.concat(' (mot ', previousMonth.sum, ' tjent i hele forrige måned)');
        }
    }

    return (<div>{message}</div>);
}


const mapStateToProps = state => {
    const earningsSumOverYear = state.earningsSumOverYear || [];
    const earningsSumOverMonth = state.earningsSumOverMonth || [];
    return {
        earningsSumOverYear,
        earningsSumOverMonth,
    };
};


EarningsMessage = connect(mapStateToProps)(EarningsMessage);
export default EarningsMessage;
