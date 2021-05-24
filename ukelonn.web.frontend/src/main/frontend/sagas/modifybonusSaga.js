import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    MODIFY_BONUS,
    RECEIVE_ALL_BONUSES,
    MODIFY_BONUS_FAILURE,
} from '../actiontypes';

// watcher saga
export default function* modifyBonusSaga() {
    yield takeLatest(MODIFY_BONUS, receiveModifyBonusSaga);
}

function doModifyBonus(bonus) {
    return axios.post('/ukelonn/api/admin/modifybonus', bonus);
}

// worker saga
function* receiveModifyBonusSaga(action) {
    try {
        const response = yield call(doModifyBonus, action.payload);
        const bonuses = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(RECEIVE_ALL_BONUSES(bonuses));
    } catch (error) {
        yield put(MODIFY_BONUS_FAILURE(error));
    }
}
