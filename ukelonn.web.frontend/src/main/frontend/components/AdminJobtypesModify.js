import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
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
    } = props;
    const dispatch = useDispatch();

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
                            <input id="name" type="text" value={transactionTypeName} onChange={e => dispatch(MODIFY_TRANSACTION_TYPE_NAME(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.modifyAmountOfJobType}</label>
                        <div>
                            <input id="amount" type="text" value={transactionAmount} onChange={e => dispatch(MODIFY_JOB_AMOUNT(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => dispatch(MODIFY_JOBTYPE_REQUEST({ id: transactionTypeId, transactionTypeName, transactionAmount }))}>{text.saveChangesToJobType}</button>
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

export default connect(mapStateToProps)(AdminJobtypesModify);
