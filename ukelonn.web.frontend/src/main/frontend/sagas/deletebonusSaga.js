import { takeLatest, call, put, fork } from 'redux-saga/effects';
import axios from 'axios';
import {
    DELETE_BONUS,
    RECEIVE_ALL_BONUSES,
    DELETE_BONUS_FAILURE,
} from '../actiontypes';

// watcher saga
export default function* deleteBonusSaga() {
    yield takeLatest(DELETE_BONUS, receiveDeleteBonusSaga);
}

function doDeleteBonus(bonus) {
    return axios.post('/ukelonn/api/admin/deletebonus', bonus);
}

// worker saga
function* receiveDeleteBonusSaga(action) {
    try {
        const response = yield call(doDeleteBonus, action.payload);
        const bonuses = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(RECEIVE_ALL_BONUSES(bonuses));
    } catch (error) {
        yield put(DELETE_BONUS_FAILURE(error));
    }
}
