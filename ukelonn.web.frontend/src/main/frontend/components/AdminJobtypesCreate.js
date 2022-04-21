import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_JOB_AMOUNT,
    CREATE_JOBTYPE_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import Logout from './Logout';

function AdminJobtypesCreate(props) {
    const {
        text,
        transactionTypeName,
        transactionAmount,
        onNameFieldChange,
        onAmountFieldChange,
        onSaveUpdatedJobType,
    } = props;

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/jobtypes">
                    &lt;-
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.createNewJobType}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="amount">{text.nameOfJobType}</label>
                        <div>
                            <input id="name" type="text" value={transactionTypeName} onChange={onNameFieldChange} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.amountForJobType}</label>
                        <div>
                            <input id="amount" type="text" value={transactionAmount} onChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => onSaveUpdatedJobType({ transactionTypeName, transactionAmount })}>{text.createNewJobType}</button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <Logout />
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        transactionTypeName: state.transactionTypeName,
        transactionAmount: state.transactionAmount,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onNameFieldChange: e => dispatch(MODIFY_TRANSACTION_TYPE_NAME(e.target.value)),
        onAmountFieldChange: e => dispatch(MODIFY_JOB_AMOUNT(e.target.value)),
        onSaveUpdatedJobType: transactiontype => dispatch(CREATE_JOBTYPE_REQUEST(transactiontype)),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobtypesCreate);
