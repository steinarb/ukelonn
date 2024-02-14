import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    MODIFY_BONUS_REQUEST,
    MODIFY_BONUS_RECEIVE,
    MODIFY_BONUS_FAILURE,
} from '../actiontypes';

function doModifyBonus(bonus) {
    return axios.post('/api/admin/modifybonus', bonus);
}

function* receiveModifyBonusSaga(action) {
    try {
        const response = yield call(doModifyBonus, action.payload);
        const bonuses = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(MODIFY_BONUS_RECEIVE(bonuses));
    } catch (error) {
        yield put(MODIFY_BONUS_FAILURE(error));
    }
}

export default function* modifyBonusSaga() {
    yield takeLatest(MODIFY_BONUS_REQUEST, receiveModifyBonusSaga);
}
