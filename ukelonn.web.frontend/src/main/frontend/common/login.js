
export function findUsername(state) {
    const loginResponse = state.loginResponse || {};
    return loginResponse.username || '';
}
