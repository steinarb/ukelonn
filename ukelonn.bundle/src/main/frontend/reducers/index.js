export const ukelonnReducer = (state = { username: null, password: null, loginResponse: { roles: [], error: '' } }, action) => {
    if (action.type == 'UPDATE') {
        return {
            ...state,
            ...action.data
        };
    }

    if (action.type === 'LOGIN_REQUEST' || action.type === 'LOGOUT_REQUEST' || action.type === 'INITIAL_LOGIN_STATE_REQUEST') {
        return {
            ...state
        };
    }

    if (action.type === 'LOGIN_RECEIVE' || action.type === 'LOGOUT_RECEIVE' || action.type === 'INITIAL_LOGIN_STATE_RECEIVE') {
        return {
            ...state,
            loginResponse: action.loginResponse
        };
    }

    return { ...state };
};
