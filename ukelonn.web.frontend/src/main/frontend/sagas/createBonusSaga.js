import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    CREATE_BONUS_REQUEST,
    CREATE_BONUS_RECEIVE,
    CREATE_BONUS_FAILURE,
} from '../actiontypes';

function doCreateBonus(bonus) {
    return axios.post('/ukelonn/api/admin/createbonus', bonus);
}

function* requestReceiveCreateBonusSaga(action) {
    try {
        const response = yield call(doCreateBonus, action.payload);
        const bonuses = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(CREATE_BONUS_RECEIVE(bonuses));
    } catch (error) {
        yield put(CREATE_BONUS_FAILURE(error));
    }
}

export default function* createBonusSaga() {
    yield takeLatest(CREATE_BONUS_REQUEST, requestReceiveCreateBonusSaga);
}
