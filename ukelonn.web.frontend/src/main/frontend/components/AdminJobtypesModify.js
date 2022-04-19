import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_JOB_AMOUNT,
    MODIFY_JOBTYPE_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import JobtypesBox from './JobtypesBox';
import Logout from './Logout';

function AdminJobtypesModify(props) {
    const {
        text,
        transactionTypeId,
        transactionAmount,
        transactionTypeName,
        onNameFieldChange,
        onAmountFieldChange,
        onSaveUpdatedJobType,
    } = props;

    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/jobtypes">
                    &lt;-
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.modifyJobTypes}</h1>
                <Locale />
            </nav>
            <div>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div>
                        <label htmlFor="jobtype">{text.chooseJobType}</label>
                        <div>
                            <JobtypesBox id="jobtype" />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.modifyNameOfJobType}</label>
                        <div>
                            <input id="name" type="text" value={transactionTypeName} onChange={onNameFieldChange} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.modifyAmountOfJobType}</label>
                        <div>
                            <input id="amount" type="text" value={transactionAmount} onChange={onAmountFieldChange} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => onSaveUpdatedJobType({ id: transactionTypeId, transactionTypeName, transactionAmount })}>{text.saveChangesToJobType}</button>
                        </div>
                    </div>
            </form>
            </div>
            <br/>
            <Logout/>
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        transactionAmount: state.transactionAmount,
        transactionTypeName: state.transactionTypeName,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onNameFieldChange: e => dispatch(MODIFY_TRANSACTION_TYPE_NAME(e.target.value)),
        onAmountFieldChange: e => dispatch(MODIFY_JOB_AMOUNT(e.target.value)),
        onSaveUpdatedJobType: jobtype => dispatch(MODIFY_JOBTYPE_REQUEST(jobtype)),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobtypesModify);
