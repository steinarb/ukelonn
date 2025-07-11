import { createSlice } from '@reduxjs/toolkit';
import { api } from '../api';
import { isClearBonusForm } from '../matchers';
import { isUnselected } from '../common/reducers';

const initialState = {
    bonusId: -1,
    enabled: false,
    iconurl: '',
    title: '',
    description: '',
    bonusFactor: 1.0,
    startDate: new Date().toISOString(),
    endDate: new Date().toISOString(),
};

export const bonusSlice = createSlice({
    name: 'bonus',
    initialState,
    reducers: {
        selectBonus: (state, action) => doSelectBonus(state, action),
        clearBonus: () => initialStateWithCurrentDate(),
        setEnabled: (state, action) => ({ ...state, enabled: !!action.payload }),
        setIconurl: (state, action) => ({ ...state, iconurl: action.payload }),
        setTitle: (state, action) => ({ ...state, title: action.payload }),
        setDescription: (state, action) => ({ ...state, description: action.payload }),
        setBonusFactor: (state, action) => ({ ...state, bonusFactor: parseFloat(action.payload) }),
        setStartDate: (state, action) => ({ ...state, startDate: action.payload + 'T' + state.startDate.split('T')[1] }),
        setEndDate: (state, action) => ({ ...state, endDate: action.payload + 'T' + state.endDate.split('T')[1] }),
    },
    extraReducers: builder => {
        builder
            .addMatcher(isClearBonusForm, (state, action) => initialStateWithCurrentDate())
    },
});

export const { selectBonus, clearBonus, setEnabled, setIconurl, setTitle, setDescription, setBonusFactor, setStartDate, setEndDate } = bonusSlice.actions;
export default bonusSlice.reducer;

function doSelectBonus(state, action) {
    const startDate = isUnselected(action.payload.bonusId) ? new Date().toISOString() : new Date(action.payload.startDate).toISOString();
    const endDate = isUnselected(action.payload.bonusId) ? new Date().toISOString() : new Date(action.payload.endDate).toISOString();
    return { ...action.payload, startDate, endDate };
}

function initialStateWithCurrentDate() {
    const startDate = new Date().toISOString();
    const endDate =new Date().toISOString();
    return { ...initialState, startDate, endDate };
}
