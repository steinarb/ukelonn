import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    DELETE_BONUS_REQUEST,
    DELETE_BONUS_RECEIVE,
    DELETE_BONUS_FAILURE,
} from '../actiontypes';

export default function* deleteBonusSaga() {
    yield takeLatest(DELETE_BONUS_REQUEST, receiveDeleteBonusSaga);
}

function* receiveDeleteBonusSaga(action) {
    try {
        const response = yield call(doDeleteBonus, action.payload);
        const bonuses = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(DELETE_BONUS_RECEIVE(bonuses));
    } catch (error) {
        yield put(DELETE_BONUS_FAILURE(error));
    }
}

function doDeleteBonus(bonus) {
    return axios.post('/api/admin/deletebonus', bonus);
}
