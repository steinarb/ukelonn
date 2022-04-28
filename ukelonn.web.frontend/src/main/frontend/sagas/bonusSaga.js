import { takeLatest, put, select } from 'redux-saga/effects';
import {
    SELECT_BONUS,
    SELECTED_BONUS,
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
        yield put(SELECTED_BONUS({ bonusId }));
    } else {
        const bonuses = yield select(state => state.allbonuses);
        const bonus = bonuses.find(u => u.bonusId === bonusId);
        if (bonus) {
            yield put(SELECTED_BONUS(bonus));
        }
    }
}

function* saveModifiedBonus() {
    const bonus = yield select(state => ({
        bonusId: state.bonusId,
        enabled: state.bonusEnabled,
        iconurl: state.bonusIconurl,
        title: state.bonusTitle,
        description: state.bonusDescription,
        bonusFactor: state.bonusFactor,
        startDate: state.bonusStartDate,
        endDate: state.bonusEndDate,
    }));
    yield put(MODIFY_BONUS_REQUEST(bonus));
}

function* saveCreatedBonus() {
    const bonus = yield select(state => ({
        enabled: state.bonusEnabled,
        title: state.bonusTitle,
        description: state.bonusDescription,
        bonusFactor: state.bonusFactor,
        startDate: state.bonusStartDate,
        endDate: state.bonusEndDate,
    }));
    yield put(CREATE_BONUS_REQUEST(bonus));
}

function* deleteSelectedBonus() {
    const bonus = yield select(state => ({
        bonusId: state.bonusId,
    }));
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
