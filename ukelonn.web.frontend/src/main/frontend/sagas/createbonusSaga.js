import { takeLatest, call, put, fork } from 'redux-saga/effects';
import axios from 'axios';
import {
    CREATE_BONUS,
    RECEIVE_ALL_BONUSES,
    CREATE_BONUS_FAILURE,
} from '../actiontypes';

// watcher saga
export default function* createbonusSaga() {
    yield takeLatest(CREATE_BONUS, receiveCreateBonusSaga);
}

function doCreateBonus(bonus) {
    return axios.post('/ukelonn/api/admin/createbonus', bonus);
}

// worker saga
function* receiveCreateBonusSaga(action) {
    try {
        const response = yield call(doCreateBonus, action.payload);
        const bonuses = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(RECEIVE_ALL_BONUSES(bonuses));
    } catch (error) {
        yield put(CREATE_BONUS_FAILURE(error));
    }
}
