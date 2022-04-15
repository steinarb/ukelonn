import { takeLatest, put, select } from 'redux-saga/effects';
import {
    SELECT_BONUS,
    MODIFY_BONUS_ENABLED,
    MODIFY_BONUS_ICONURL,
    MODIFY_BONUS_TITLE,
    MODIFY_BONUS_DESCRIPTION,
    MODIFY_BONUS_FACTOR,
    MODIFY_BONUS_START_DATE,
    MODIFY_BONUS_END_DATE,
    SAVE_BONUS_CHANGES_BUTTON_CLICKED,
    MODIFY_BONUS_REQUEST,
    MODIFY_BONUS_RECEIVE,
    CREATE_NEW_BONUS_BUTTON_CLICKED,
    CREATE_BONUS_REQUEST,
    CREATE_BONUS_RECEIVE,
    DELETE_SELECTED_BONUS_BUTTON_CLICKED,
    DELETE_BONUS_REQUEST,
    DELETE_BONUS_RECEIVE,
    CLEAR_BONUS,
} from '../actiontypes';

function* selectBonus(action) {
    const bonusId = action.payload;
    if (bonusId === -1) {
        yield put(CLEAR_BONUS());
    } else {
        const bonuses = yield select(state => state.allbonuses);
        const bonus = bonuses.find(u => u.bonusId === bonusId);
        if (bonus) {
            yield put(MODIFY_BONUS_ENABLED(bonus.enabled));
            yield put(MODIFY_BONUS_ICONURL(bonus.iconurl));
            yield put(MODIFY_BONUS_TITLE(bonus.title));
            yield put(MODIFY_BONUS_DESCRIPTION(bonus.description));
            yield put(MODIFY_BONUS_FACTOR(bonus.bonusFactor));
            yield put(MODIFY_BONUS_START_DATE(bonus.startDate));
            yield put(MODIFY_BONUS_END_DATE(bonus.endDate));
        }
    }
}

function* saveModifiedBonus() {
    const bonusId = yield select(state => state.bonusId);
    const enabled = yield select(state => state.bonusEnabled);
    const title = yield select(state => state.bonusTitle);
    const description = yield select(state => state.bonusDescription);
    const bonusFactor = yield select(state => state.bonusFactor);
    const startDate = yield select(state => state.bonusStartDate);
    const endDate = yield select(state => state.bonusEndDate);
    const bonus = {
        bonusId,
        enabled,
        title,
        description,
        bonusFactor,
        startDate,
        endDate,
    };
    yield put(MODIFY_BONUS_REQUEST(bonus));
}

function* saveCreatedBonus() {
    const enabled = yield select(state => state.bonusEnabled);
    const title = yield select(state => state.bonusTitle);
    const description = yield select(state => state.bonusDescription);
    const bonusFactor = yield select(state => state.bonusFactor);
    const startDate = yield select(state => state.bonusStartDate);
    const endDate = yield select(state => state.bonusEndDate);
    const bonus = {
        enabled,
        title,
        description,
        bonusFactor,
        startDate,
        endDate,
    };
    yield put(CREATE_BONUS_REQUEST(bonus));
}

function* deleteSelectedBonus() {
    const bonusId = yield select(state => state.bonusId);
    const bonus = {
        bonusId,
    };
    yield put(DELETE_BONUS_REQUEST(bonus));
}

function* clearBonusForm() {
    yield put(CLEAR_BONUS());
}

export default function* bonusSaga() {
    yield takeLatest(SELECT_BONUS, selectBonus);
    yield takeLatest(SAVE_BONUS_CHANGES_BUTTON_CLICKED, saveModifiedBonus);
    yield takeLatest(CREATE_NEW_BONUS_BUTTON_CLICKED, saveCreatedBonus);
    yield takeLatest(DELETE_SELECTED_BONUS_BUTTON_CLICKED, deleteSelectedBonus);
    yield takeLatest(MODIFY_BONUS_RECEIVE, clearBonusForm);
    yield takeLatest(CREATE_BONUS_RECEIVE, clearBonusForm);
    yield takeLatest(DELETE_BONUS_RECEIVE, clearBonusForm);
}
