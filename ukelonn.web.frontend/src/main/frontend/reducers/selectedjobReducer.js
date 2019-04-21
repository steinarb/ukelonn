import { createReducer } from 'redux-starter-kit';
import {
    UPDATE,
} from '../actiontypes';
import { emptyTransaction } from './constants';

const selectedjobReducer = createReducer({ ...emptyTransaction }, {
    [UPDATE]: (state, action) => {
        if (!action.payload) { return state; }
        const selectedjob = action.payload.selectedjob;
        if (selectedjob === undefined) { return state; }
        return selectedjob;
    },
});

export default selectedjobReducer;
