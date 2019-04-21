import { createReducer } from 'redux-starter-kit';
import moment from 'moment';
import {
    UPDATE,
    REGISTERJOB_RECEIVE,
} from '../actiontypes';
import { emptyPerformedTransaction } from './constants';

const performedjobReducer = createReducer({ ...emptyPerformedTransaction }, {
    [UPDATE]: (state, action) => {
        if (!action.payload) { return state; }
        const performedjob = action.payload.performedjob;
        if (performedjob === undefined) { return state; }
        return { ...state, ...performedjob };
    },
    [REGISTERJOB_RECEIVE]: (state, action) => ({ ...emptyPerformedTransaction, transactionName: '', transactionDate: moment() }),
});

export default performedjobReducer;
